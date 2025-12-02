package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.EstadoProducto;
import com.PPA.procesadora.entity.PreTarima;
import com.PPA.procesadora.entity.Produccion;
import com.PPA.procesadora.entity.ProduccionAlm;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.EstadoProductoRepository;
import com.PPA.procesadora.repository.PreTarimaRepository;
import com.PPA.procesadora.repository.ProduccionAlmRepository;
import com.PPA.procesadora.repository.ProduccionRepository;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreTarimaService {

    private final PreTarimaRepository preTarimaRepository;
    private final ProduccionAlmRepository produccionAlmRepository;
    private final EstadoProductoRepository estadoProductoRepository;
    private final PDFGeneratorService pdfGeneratorService;
    private final PrinterService printerService;

    @Value("${printer.ip:128.23.35.80}")
    private String printerIp;

    @Value("${printer.port:9100}")
    private int printerPort;

    @Value("${printer.auto-print:true}")
    private boolean autoPrintEnabled;

    /**
     * Crea una pre-tarima a partir del código escaneado de la caja
     *
     * Formato escaneado: *0 + codigoProducto + fecha + *
     * Ej: *0242052251125*
     *
     * Proceso:
     * 1. Parsea el código escaneado
     * 2. Valida que exista producción ACTIVA con ese producto
     * 3. Obtiene el estado del producto (default: TERMINADO)
     * 4. Genera número de tarima único (secuencial por ProduccionAlm)
     * 5. Genera código de pre-tarima: T001-PBG1-251125
     * 6. Guarda y retorna
     */
    @Transactional
    public PreTarimaDTO crearPreTarimaDesdeCodigoEscaneado(String codigoEscaneado, String estadoProductoCodigo, Usuario usuario) {

        // Paso 1: Parsear código escaneado
        CodigoEscaneadoDTO codigoParseado = parsearCodigoEscaneado(codigoEscaneado);

        // Paso 2: Validar que existe producción activa para ese producto
        ProduccionAlm produccionAlm = produccionAlmRepository.findProduccionActivaByProductoCodigo(codigoParseado.codigoProducto, "ACTIVA")
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay producción activa para el producto: " + codigoParseado.codigoProducto));

        // Paso 3: Obtener estado del producto
        EstadoProducto estadoProducto = estadoProductoRepository.findByCodigo(estadoProductoCodigo)
                .orElseThrow(() -> new IllegalArgumentException("Estado de producto no encontrado: " + estadoProductoCodigo));
        // Obtener la fecha de la Producción asociada y formatearla a ddMMyy

        LocalDateTime fechaProduccionLocal = Optional.ofNullable(produccionAlm.getProduccion())
                .map(Produccion::getFecha)
                .orElseThrow(() -> new IllegalStateException("La Produccion asociada no tiene fecha"));

        // Paso 4: Capturar horaInicio en ProduccionAlm si es la primera PreTarima
        if (produccionAlm.getHoraInicio() == null) {
            produccionAlm.setHoraInicio(java.time.LocalDateTime.now());
            produccionAlmRepository.save(produccionAlm);
            log.info("Hora de inicio capturada en ProduccionAlm: {}", produccionAlm.getCodigo());
        }
        DateTimeFormatter ddMMyy = DateTimeFormatter.ofPattern("ddMMyy");
        String fechaProduccion = fechaProduccionLocal.format(ddMMyy);

        // Paso 5: Generar número de tarima único
        Integer numeroTarima = generarNumeroTarimaSiguiente(produccionAlm);

        // Paso 5: Generar código de pre-tarima
        String codigoPreTarima = generarCodigoPreTarima(
                numeroTarima,
                produccionAlm.getCodigo(),
                fechaProduccion);

        // Paso 6: Crear y guardar pre-tarima
        PreTarima preTarima = new PreTarima();
        preTarima.setProduccionAlm(produccionAlm);
        preTarima.setProduccion(produccionAlm.getProduccion());
        preTarima.setCantidadCajas(25);
        preTarima.setEstadoProducto(estadoProducto);
        preTarima.setCodigo(codigoPreTarima);
        preTarima.setHoraCreacion(DateUtils.ahora());
        preTarima.setFechaCreacion(DateUtils.ahora());
        preTarima.setUsuario(usuario);

        PreTarima guardada = preTarimaRepository.save(preTarima);

        // Crear DTO de respuesta
        PreTarimaDTO respuesta = new PreTarimaDTO(
                guardada.getId(),
                guardada.getCodigo(),
                guardada.getProduccionAlm().getProducto().getNombre(),
                guardada.getProduccionAlm().getProducto().getAbreviacion(),
                guardada.getCantidadCajas(),
                DateUtils.formatear(guardada.getHoraCreacion()),
                estadoProducto.getCodigo(),
                "CREADA");

        // Paso 7: Generar e imprimir PDF de forma asincrónica
        if (autoPrintEnabled) {
            imprimirPreTarimaAsincrono(guardada);
        }

        return respuesta;
    }

    /**
     * Obtiene información de una pre-tarima por código
     */
    @Transactional(readOnly = true)
    public PreTarimaDTO getPreTarimaInfo(String codigo) {
        PreTarima preTarima = preTarimaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Pre-tarima no encontrada"));

        // Verificar vigencia (máximo 2 horas)
        if (!DateUtils.esPreTarimaVigente(preTarima.getFechaCreacion())) {
            throw new IllegalArgumentException("Pre-tarima expirada (máximo 2 horas)");
        }

        return new PreTarimaDTO(
                preTarima.getId(),
                preTarima.getCodigo(),
                preTarima.getProduccionAlm().getProducto().getNombre(),
                preTarima.getProduccionAlm().getProducto().getAbreviacion(),
                preTarima.getCantidadCajas(),
                DateUtils.formatear(preTarima.getHoraCreacion()),
                preTarima.getEstadoProducto().getCodigo(),
                "VIGENTE");
    }

    /**
     * Parsea el código escaneado de la caja
     * Formato: 0 + codigoProducto(6dig) + fecha(6dig: ddMMYY)
     * El scanner elimina automáticamente los asteriscos
     * Ej: 0242052251125 (sin asteriscos)
     */
    private CodigoEscaneadoDTO parsearCodigoEscaneado(String codigoEscaneado) {
        if (codigoEscaneado == null || codigoEscaneado.isBlank()) {
            throw new IllegalArgumentException("Código escaneado vacío");
        }

        // Limpiar espacios
        String codigo = codigoEscaneado.trim();

        // Remover asteriscos si los hay (en caso de lectura manual)
        codigo = codigo.replaceAll("\\*", "");

        // Validar que comience con "0"
        if (!codigo.startsWith("0")) {
            throw new IllegalArgumentException("Código inválido. Debe comenzar con 0");
        }

        // Remover el prefijo "0"
        String contenido = codigo.substring(1);

        // Validar longitud mínima (6 dígitos código + 6 dígitos fecha = 12)
        if (contenido.length() < 12) {
            throw new IllegalArgumentException("Código muy corto. Debe tener al menos 12 dígitos después del prefijo");
        }

        // Extraer código de producto (primeros 6 dígitos)
        String codigoProducto = contenido.substring(0, 6);

        // Validar que sean números (solo para el código de producto)
        if (!codigoProducto.matches("\\d{6}")) {
            throw new IllegalArgumentException("Código de producto inválido (debe tener 6 dígitos)");
        }

        // No extraemos ni validamos la fecha del scanner aquí.
        // La fecha la obtenemos desde la Producción activa asociada a la ProduccionAlm
        return new CodigoEscaneadoDTO(codigoProducto, null);
    }


    /**
     * Genera el siguiente número de tarima para esta ProduccionAlm específica
     * Formato: T001, T002, T003... (secuencial por ProduccionAlm)
     * Cuando termina una ProduccionAlm y empieza otra, vuelve a contar desde T001
     */
    private Integer generarNumeroTarimaSiguiente(ProduccionAlm produccionAlm) {
        // Obtener el máximo número de tarima para esta ProduccionAlm específica
        Optional<PreTarima> ultimaPreTarima = preTarimaRepository.findTopByProduccionAlmOrderByIdDesc(produccionAlm);

        if (ultimaPreTarima.isPresent()) {
            // Extraer el número de la última pre-tarima
            // Formato: T001-PBG1-251125 → extraer 001
            String codigo = ultimaPreTarima.get().getCodigo();
            String numeroStr = codigo.substring(1, 4); // Posiciones 1-3: "001"
            Integer numero = Integer.parseInt(numeroStr);
            return numero + 1;
        }
        return 1; // Primera tarima de esta ProduccionAlm
    }

    /**
     * Genera código único de pre-tarima
     * Formato: T + numeroTarima(3dig) - codigoProduccionAlm - fechaProduccion(ddMMYY)
     * Ej: T001-PBG1-251125
     *
     * Donde:
     * - T001 = número secuencial de tarima (ej: primera tarima = T001, segunda = T002)
     * - PBG1 = código de ProduccionAlm
     * - 251125 = fecha de Produccion (ddMMYY)
     */
    private String generarCodigoPreTarima(Integer numeroTarima, String codigoProduccionAlm, String fechaProduccion) {
        return String.format("T%03d-%s-%s",
                numeroTarima,
                codigoProduccionAlm,
                fechaProduccion);
    }

    /**
     * DTO interno para almacenar datos parseados del código escaneado
     */
    private static class CodigoEscaneadoDTO {
        String codigoProducto;
        String fechaProduccion;

        CodigoEscaneadoDTO(String codigoProducto, String fechaProduccion) {
            this.codigoProducto = codigoProducto;
            this.fechaProduccion = fechaProduccion;
        }
    }

    /**
     * DTO para respuesta de pre-tarima
     */
    public static class PreTarimaDTO {
        public Integer id;
        public String codigo;
        public String nombreProducto;
        public String abreviacionProducto;
        public Integer cantidadCajas;
        public String horaCreacion;
        public String estadoProducto;
        public String estado;

        public PreTarimaDTO(Integer id, String codigo, String nombreProducto, String abreviacionProducto,
                            Integer cantidadCajas, String horaCreacion, String estadoProducto, String estado) {
            this.id = id;
            this.codigo = codigo;
            this.nombreProducto = nombreProducto;
            this.abreviacionProducto = abreviacionProducto;
            this.cantidadCajas = cantidadCajas;
            this.horaCreacion = horaCreacion;
            this.estadoProducto = estadoProducto;
            this.estado = estado;
        }

        public Integer getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getNombreProducto() { return nombreProducto; }
        public String getAbreviacionProducto() { return abreviacionProducto; }
        public Integer getCantidadCajas() { return cantidadCajas; }
        public String getHoraCreacion() { return horaCreacion; }
        public String getEstadoProducto() { return estadoProducto; }
        public String getEstado() { return estado; }
    }

    /**
     * Imprime la pre-tarima de forma asincrónica
     */
    private void imprimirPreTarimaAsincrono(PreTarima preTarima) {
        new Thread(() -> {
            try {
                Thread.sleep(500);

                log.info("Generando PDF para pre-tarima: {}", preTarima.getCodigo());
                byte[] pdfBytes = pdfGeneratorService.generarPDFPreTarima(preTarima, 2);

                log.info("Enviando a imprimir: {} a {}:{}", preTarima.getCodigo(), printerIp, printerPort);
                boolean impreso = printerService.imprimirPDFaImpresora(printerIp, printerPort, pdfBytes, preTarima.getCodigo());

                if (impreso) {
                    log.info("✓ Pre-tarima impresa exitosamente: {}", preTarima.getCodigo());
                } else {
                    log.warn("⚠ Fallo al imprimir pre-tarima: {}", preTarima.getCodigo());
                }
            } catch (Exception e) {
                log.error("Error en impresión asincrónica: {}", e.getMessage(), e);
            }
        }).start();
    }
}