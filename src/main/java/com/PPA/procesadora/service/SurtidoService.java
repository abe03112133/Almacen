package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.*;
import com.PPA.procesadora.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICIO: SurtidoService
 *
 * Orquesta todo el flujo de surtidos:
 * 1. Crear SurtidoDiario
 * 2. Agregar SurtidoProducto (editable)
 * 3. Generar DetalleSurtido (PEPS automático)
 * 4. Descargar picklist PDF
 * 5. Registrar diferencias (complementarios, dañadas, no bajadas)
 * 6. Cerrar SurtidoDiario
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SurtidoService {

    private final SurtidoDiarioRepository surtidoDiarioRepository;
    private final SurtidoProductoRepository surtidoProductoRepository;
    private final DetalleSurtidoRepository detalleSurtidoRepository;
    private final DestinoRepository destinoRepository;
    private final TarimaRepository tarimaRepository;
    private final EspacioRepository espacioRepository;
    private final TipoRackRepository tipoRackRepository;
    private final ProductoRepository productoRepository;

    // ===================================================================
    // 1. CREAR SURTIDO DIARIO
    // ===================================================================

    /**
     * Crea un nuevo SurtidoDiario en estado ACTIVO
     */
    public SurtidoDiario crearSurtidoDiario(LocalDateTime fecha, String descripcion, Usuario supervisor) {
        SurtidoDiario sd = new SurtidoDiario();
        sd.setFecha(fecha);
        sd.setDescripcion(descripcion);
        sd.setSupervisorAsignado(supervisor);
        sd.setEstado("ACTIVO");
        sd.setFechaCreacion(LocalDateTime.now());
        sd.setActivo(true);

        SurtidoDiario saved = surtidoDiarioRepository.save(sd);
        log.info("✓ SurtidoDiario creado: ID={}, fecha={}, supervisor={}",
                saved.getId(), fecha, supervisor.getUsername());
        return saved;
    }

    // ===================================================================
    // 2. AGREGAR SURTIDO PRODUCTO (EDITABLE)
    // ===================================================================

    /**
     * Agrega un SurtidoProducto a un SurtidoDiario
     * Puede hacerse en estado ACTIVO, antes de generar picklist
     */
    public SurtidoProducto agregarSurtidoProducto(
            Integer surtidoDiarioId,
            Integer productoId,
            Integer destinoId,
            Integer cantidadSolicitada,
            String observaciones) {

        SurtidoDiario sd = surtidoDiarioRepository.findById(surtidoDiarioId)
                .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

        if (!sd.getEstado().equals("ACTIVO")) {
            throw new RuntimeException("Solo se pueden agregar productos a SurtidoDiario ACTIVO");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        SurtidoProducto sp = new SurtidoProducto();
        sp.setSurtidoDiario(sd);
        sp.setProducto(producto);
        sp.setDestino(destino);
        sp.setCantidadSolicitada(cantidadSolicitada);
        sp.setEstado("ACTIVO");
        sp.setFechaCreacion(LocalDateTime.now());
        sp.setObservaciones(observaciones);

        SurtidoProducto saved = surtidoProductoRepository.save(sp);
        log.info("✓ SurtidoProducto agregado: ID={}, producto={}, cantidad={}, destino={}",
                saved.getId(), productoId, cantidadSolicitada, destino.getNombre());
        return saved;
    }

    /**
     * Edita un SurtidoProducto ANTES de generar picklist
     */
    public SurtidoProducto editarSurtidoProducto(
            Integer surtidoProductoId,
            Integer cantidadSolicitada,
            Integer destinoId,
            String observaciones) {

        SurtidoProducto sp = surtidoProductoRepository.findById(surtidoProductoId)
                .orElseThrow(() -> new RuntimeException("SurtidoProducto no encontrado"));

        if (!"ACTIVO".equals(sp.getEstado()) && !"EDITADO".equals(sp.getEstado())) {
            throw new RuntimeException("Solo se pueden editar SurtidoProducto en estado ACTIVO o EDITADO");
        }

        // Si ya tiene detalles asignados, no se puede cambiar cantidad
        if (sp.getCantidadAsignada() > 0) {
            throw new RuntimeException("No se puede cambiar cantidad cuando ya hay detalle asignado. " +
                    "Primero debe limpiar el picklist o crear complementario.");
        }

        sp.setCantidadSolicitada(cantidadSolicitada);
        if (destinoId != null) {
            Destino destino = destinoRepository.findById(destinoId)
                    .orElseThrow(() -> new RuntimeException("Destino no encontrado"));
            sp.setDestino(destino);
        }
        sp.setObservaciones(observaciones);
        sp.setEstado("EDITADO");
        sp.setFechaEdicion(LocalDateTime.now());

        SurtidoProducto saved = surtidoProductoRepository.save(sp);
        log.info("✓ SurtidoProducto editado: ID={}, nuevaCantidad={}", saved.getId(), cantidadSolicitada);
        return saved;
    }

    // ===================================================================
    // 3. GENERAR DETALLE SURTIDO (LÓGICA PEPS)
    // ===================================================================

    /**
     * Genera DetalleSurtido aplicando PEPS automático
     *
     * Algoritmo PEPS:
     * 1. Busca espacios ocupados con tarimas del producto
     * 2. Ordena por fecha_ocupacion ASC (más antiguas primero)
     * 3. Asigna a DetalleSurtido hasta cumplir cantidad solicitada
     * 4. Marca como PENDIENTE
     *
     * IMPORTANTE: Solo busca en RACKS, NO en PISO
     */
    public List<DetalleSurtido> generarPicklist(Integer surtidoProductoId) {
        SurtidoProducto sp = surtidoProductoRepository.findById(surtidoProductoId)
                .orElseThrow(() -> new RuntimeException("SurtidoProducto no encontrado"));

        if (!sp.getEstado().equals("ACTIVO") && !sp.getEstado().equals("EDITADO")) {
            throw new RuntimeException("Solo se pueden generar detalles para ACTIVO o EDITADO");
        }

        // Limpiar detalles anteriores si los hay
        List<DetalleSurtido> detallesAnteriores = detalleSurtidoRepository.findBySurtidoProducto(sp);
        if (!detallesAnteriores.isEmpty()) {
            log.warn("⚠ Limpiando {} detalles anteriores para regenerar picklist", detallesAnteriores.size());
            detalleSurtidoRepository.deleteAll(detallesAnteriores);
            sp.setCantidadAsignada(0);
            sp.setCantidadSurtida(0);
        }

        // 1. Buscar espacios ocupados con tarimas del producto
        List<Espacio> espaciosDisponibles = buscarEspaciosPorProductoPEPS(sp.getProducto().getId());

        log.info("🔍 PEPS: Encontrados {} espacios con producto {}",
                espaciosDisponibles.size(), sp.getProducto().getCodigo());

        // 2. Crear DetalleSurtido hasta cumplir cantidad
        List<DetalleSurtido> detalles = new ArrayList<>();
        Integer cantidadAsignada = 0;

        for (Espacio espacio : espaciosDisponibles) {
            if (cantidadAsignada >= sp.getCantidadSolicitada()) {
                break;
            }

            Tarima tarima = espacio.getTarima();
            if (tarima == null) continue;

            DetalleSurtido detalle = new DetalleSurtido();
            detalle.setSurtidoProducto(sp);
            detalle.setTarima(tarima);
            detalle.setEspacioOrigen(espacio);
            detalle.setEstado("PENDIENTE");
            detalle.setFechaAsignacion(LocalDateTime.now());
            detalle.setCantidadTarimas(1);

            // Capturar datos del espacio para el picklist
            detalle.setNumeroRackOrigen(espacio.getRack().getNumero());
            detalle.setNivelOrigen(espacio.getNivel());
            detalle.setLadoOrigen(espacio.getLado());
            detalle.setPosicionOrigen(espacio.getPosicion());
            detalle.setCodigoQrOrigen(espacio.getCodigoQr());

            DetalleSurtido saved = detalleSurtidoRepository.save(detalle);
            detalles.add(saved);
            cantidadAsignada++;
        }

        // Actualizar SurtidoProducto con cantidad asignada
        sp.setCantidadAsignada(cantidadAsignada);
        sp.setEstado("GENERADO");
        sp.setFechaGeneracionPicklist(LocalDateTime.now());
        surtidoProductoRepository.save(sp);

        log.info("✓ Picklist generado: SurtidoProducto={}, detalles={}, cantidad asignada={}/{}",
                sp.getId(), detalles.size(), cantidadAsignada, sp.getCantidadSolicitada());

        return detalles;
    }

    /**
     * ALGORITMO PEPS: Busca espacios ordenados por fecha de ocupación (más antiguo primero)
     *
     * Busca solo en RACKS con tarimas del producto, NO en PISO
     */
    private List<Espacio> buscarEspaciosPorProductoPEPS(Integer productoId) {
        // Usar query nativa para obtener espacios ordenados por fecha PEPS
        List<Espacio> espacios = espacioRepository.findEspaciosDisponiblesPorProductoOrderByFechaPEPS(productoId);
        return espacios;
    }

    // ===================================================================
    // 4. REGISTRAR DIFERENCIAS (COMPLEMENTARIOS, DAÑADAS, NO BAJADAS)
    // ===================================================================

    /**
     * Registra tarimas EXTRA bajadas (SurtidoComplementario)
     *
     * Cuando el surtidor baja más tarimas de las asignadas, se crea otro
     * SurtidoProducto vinculado al mismo SurtidoDiario como complementario
     */
    public SurtidoProducto registrarComplementario(
            Integer surtidoDiarioId,
            Integer productoId,
            Integer destinoId,
            Integer cantidadExtra) {

        SurtidoDiario sd = surtidoDiarioRepository.findById(surtidoDiarioId)
                .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        // Crear nuevo SurtidoProducto complementario
        SurtidoProducto complementario = new SurtidoProducto();
        complementario.setSurtidoDiario(sd);
        complementario.setProducto(producto);
        complementario.setDestino(destino);
        complementario.setCantidadSolicitada(cantidadExtra);
        complementario.setEstado("ACTIVO");
        complementario.setEsComplementario(true);
        complementario.setFechaCreacion(LocalDateTime.now());
        complementario.setObservaciones("Complementario - tarimas extra bajadas en surtido");

        SurtidoProducto saved = surtidoProductoRepository.save(complementario);
        log.info("✓ Complementario registrado: ID={}, cantidad={}", saved.getId(), cantidadExtra);
        return saved;
    }

    /**
     * Registra una tarima DAÑADA en la ejecución
     *
     * 1. Cambiar estado a DAÑADA en DetalleSurtido
     * 2. Mover tarima de su espacio a PISO
     * 3. Liberar espacio anterior
     */
    public void registrarTarimaDañada(Integer detalleSurtidoId, String motivo) {
        DetalleSurtido detalle = detalleSurtidoRepository.findById(detalleSurtidoId)
                .orElseThrow(() -> new RuntimeException("DetalleSurtido no encontrado"));

        Tarima tarima = detalle.getTarima();
        Espacio espacioActual = tarima.getEspacio();

        // 1. Marcar como DAÑADA en detalle
        detalle.setEstado("DAÑADA");
        detalle.setFechaEjecucion(LocalDateTime.now());
        detalle.setObservaciones("DAÑADA: " + motivo);
        detalleSurtidoRepository.save(detalle);

        // 2. Mover tarima a PISO
        Espacio espacioPiso = buscarEspacioPisoDisponible();
        if (espacioPiso == null) {
            throw new RuntimeException("No hay espacios PISO disponibles para tarimas dañadas");
        }

        tarima.setEspacio(espacioPiso);
        espacioPiso.setTarima(tarima);
        espacioPiso.setEstado("OCUPADO");
        espacioPiso.setFechaOcupacion(LocalDateTime.now());

        // 3. Liberar espacio anterior
        if (espacioActual != null) {
            espacioActual.setTarima(null);
            espacioActual.setEstado("DISPONIBLE");
            espacioActual.setFechaLiberacion(LocalDateTime.now());
            espacioRepository.save(espacioActual);
        }

        espacioRepository.save(espacioPiso);
        tarimaRepository.save(tarima);

        log.info("✓ Tarima {} movida a PISO (DAÑADA): {}", tarima.getCodigo(), motivo);
    }

    /**
     * Registra una tarima NO BAJADA
     *
     * 1. Cambiar estado a NO_BAJADA en DetalleSurtido
     * 2. Tarima regresa a estado normal DISPONIBLE (sin cambios en BD)
     */
    public void registrarTarimaNoBajada(Integer detalleSurtidoId, String motivo) {
        DetalleSurtido detalle = detalleSurtidoRepository.findById(detalleSurtidoId)
                .orElseThrow(() -> new RuntimeException("DetalleSurtido no encontrado"));

        detalle.setEstado("NO_BAJADA");
        detalle.setFechaEjecucion(LocalDateTime.now());
        detalle.setObservaciones("NO BAJADA: " + motivo);
        detalleSurtidoRepository.save(detalle);

        log.info("✓ Tarima {} registrada como NO BAJADA: {}", detalle.getTarima().getCodigo(), motivo);
        // Tarima permanece en su espacio, disponible para próximos surtidos
    }

    /**
     * Marca una tarima como SURTIDA
     */
    public void marcarTarimaSurtida(Integer detalleSurtidoId) {
        DetalleSurtido detalle = detalleSurtidoRepository.findById(detalleSurtidoId)
                .orElseThrow(() -> new RuntimeException("DetalleSurtido no encontrado"));

        detalle.setEstado("SURTIDO");
        detalle.setFechaEjecucion(LocalDateTime.now());
        detalleSurtidoRepository.save(detalle);

        // Incrementar cantidad surtida en SurtidoProducto
        SurtidoProducto sp = detalle.getSurtidoProducto();
        sp.setCantidadSurtida(sp.getCantidadSurtida() + 1);
        surtidoProductoRepository.save(sp);

        log.info("✓ Tarima {} marcada como SURTIDA", detalle.getTarima().getCodigo());
    }

    // ===================================================================
    // 5. CERRAR SURTIDO DIARIO
    // ===================================================================

    /**
     * Cierra un SurtidoDiario (ACTIVO -> TERMINADO)
     */
    public SurtidoDiario cerrarSurtidoDiario(Integer surtidoDiarioId) {
        SurtidoDiario sd = surtidoDiarioRepository.findById(surtidoDiarioId)
                .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

        if (!sd.getEstado().equals("ACTIVO")) {
            throw new RuntimeException("Solo se pueden cerrar SurtidoDiario en estado ACTIVO");
        }

        sd.cerrar();
        SurtidoDiario saved = surtidoDiarioRepository.save(sd);
        log.info("✓ SurtidoDiario cerrado: ID={}, fecha cierre={}", saved.getId(), saved.getFechaCierre());
        return saved;
    }

    // ===================================================================
    // 6. UTILIDADES
    // ===================================================================

    /**
     * Busca un espacio PISO disponible para tarimas dañadas
     */
    private Espacio buscarEspacioPisoDisponible() {
        TipoRack tipoRackPiso = tipoRackRepository.findByCodigo("PISO").get();
        if (tipoRackPiso == null) {
            throw new RuntimeException("TipoRack PISO no encontrado en BD");
        }

        return espacioRepository.findFirstByRack_TipoRack_IdAndEstadoOrderByCodigoQr(
                tipoRackPiso.getId(), "DISPONIBLE");
    }

    /**
     * Obtiene resumen de un SurtidoDiario
     */
    public Map<String, Object> obtenerResumenSurtidoDiario(Integer surtidoDiarioId) {
        SurtidoDiario sd = surtidoDiarioRepository.findById(surtidoDiarioId)
                .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

        List<SurtidoProducto> productos = surtidoProductoRepository.findBySurtidoDiario(sd);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("id", sd.getId());
        resumen.put("fecha", sd.getFecha());
        resumen.put("estado", sd.getEstado());
        resumen.put("totalProductos", productos.size());

        int totalSolicitado = productos.stream().mapToInt(SurtidoProducto::getCantidadSolicitada).sum();
        int totalAsignado = productos.stream().mapToInt(SurtidoProducto::getCantidadAsignada).sum();
        int totalSurtido = productos.stream().mapToInt(SurtidoProducto::getCantidadSurtida).sum();

        resumen.put("totalSolicitado", totalSolicitado);
        resumen.put("totalAsignado", totalAsignado);
        resumen.put("totalSurtido", totalSurtido);
        resumen.put("porcentajeSurtido", totalAsignado > 0 ? (totalSurtido * 100 / totalAsignado) : 0);

        return resumen;
    }
}