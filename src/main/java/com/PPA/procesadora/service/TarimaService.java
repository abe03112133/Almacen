package com.PPA.procesadora.service;

import com.PPA.procesadora.dto.TarimaDTO;
import com.PPA.procesadora.entity.Espacio;
import com.PPA.procesadora.entity.PreTarima;
import com.PPA.procesadora.entity.Tarima;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.EspacioRepository;
import com.PPA.procesadora.repository.PreTarimaRepository;
import com.PPA.procesadora.repository.TarimaRepository;
import com.PPA.procesadora.util.Constants;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarimaService {

    private final TarimaRepository tarimaRepository;
    private final PreTarimaRepository preTarimaRepository;
    private final EspacioRepository espacioRepository;
    private final EspacioService espacioService;

    /**
     * Crea una tarima a partir de una pre-tarima
     * Estado inicial: ESPERANDO SLOT
     */
    @Transactional
    public TarimaDTO crearTarima(String codigoPreTarima, Usuario usuario) {
        PreTarima preTarima = preTarimaRepository.findByCodigo(codigoPreTarima)
                .orElseThrow(() -> new IllegalArgumentException("Pre-tarima no encontrada"));

        // Verificar vigencia
        if (!DateUtils.esPreTarimaVigente(preTarima.getFechaCreacion())) {
            throw new IllegalArgumentException("Pre-tarima expirada");
        }

        // Crear tarima
        Tarima tarima = new Tarima();
        tarima.setPreTarima(preTarima);
        tarima.setCodigo(preTarima.getCodigo());
        tarima.setFechaAlmacen(DateUtils.ahora());
        tarima.setUsuarioAlmacen(usuario);

        Tarima guardada = tarimaRepository.save(tarima);

        return new TarimaDTO(
                guardada.getId(),
                guardada.getCodigo(),
                preTarima.getProduccionAlm().getProducto().getNombre(),
                preTarima.getCantidadCajas(),
                DateUtils.formatear(guardada.getFechaAlmacen()),
                "ESPERANDO SLOT",
                null
        );
    }

    /**
     * Guarda una tarima en un espacio específico
     */
    @Transactional
    public TarimaDTO guardarTarimaEnEspacio(Integer tarimaId, Integer espacioId, Usuario usuario) {
        Tarima tarima = tarimaRepository.findById(tarimaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarima no encontrada"));

        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        // Validar que el espacio esté disponible
        if (!Constants.ESTADO_DISPONIBLE.equals(espacio.getEstado())) {
            throw new IllegalArgumentException("El espacio no está disponible");
        }

        // Guardar tarima en espacio
        tarima.setEspacio(espacio);
        espacio.setTarima(tarima);
        espacio.setEstado(Constants.ESTADO_OCUPADO);
        espacio.setFechaOcupacion(DateUtils.ahora());
        espacio.setFechaOcupacion(DateUtils.ahora());

        espacioRepository.save(espacio);
        Tarima guardada = tarimaRepository.save(tarima);

        return new TarimaDTO(
                guardada.getId(),
                guardada.getCodigo(),
                guardada.getPreTarima().getProduccionAlm().getProducto().getNombre(),
                guardada.getPreTarima().getCantidadCajas(),
                DateUtils.formatear(guardada.getFechaAlmacen()),
                "ALMACENADA",
                formatearUbicacion(espacio)
        );
    }

    /**
     * Obtiene tarimas no surtidas (para reportes/surtido)
     */
    @Transactional(readOnly = true)
    public List<Tarima> obtenerTarimasNoSurtidas() {
        return tarimaRepository.findTarimasNoSurtidas();
    }

    /**
     * Libera una tarima (marca como surtida)
     */
    @Transactional
    public void marcarComosurtida(Integer tarimaId, Usuario usuario) {
        Tarima tarima = tarimaRepository.findById(tarimaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarima no encontrada"));

        if (tarima.getFechaSurtido() != null) {
            throw new IllegalArgumentException("La tarima ya fue surtida");
        }

        tarima.setFechaSurtido(DateUtils.ahora());
        tarima.setUsuarioSurtido(usuario);

        // Liberar espacio
        if (tarima.getEspacio() != null) {
            Espacio espacio = tarima.getEspacio();
            espacio.setTarima(null);
            espacio.setEstado(Constants.ESTADO_DISPONIBLE);
            espacio.setFechaLiberacion(DateUtils.ahora());
            espacioRepository.save(espacio);
        }

        tarimaRepository.save(tarima);
    }

    /**
     * Obtiene información de una tarima
     */
    @Transactional(readOnly = true)
    public TarimaDTO obtenerInfoTarima(Integer tarimaId) {
        Tarima tarima = tarimaRepository.findById(tarimaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarima no encontrada"));

        String ubicacion = null;
        if (tarima.getEspacio() != null) {
            ubicacion = formatearUbicacion(tarima.getEspacio());
        }

        String estado = tarima.getFechaSurtido() != null ? "SURTIDA" : "ALMACENADA";

        return new TarimaDTO(
                tarima.getId(),
                tarima.getCodigo(),
                tarima.getPreTarima().getProduccionAlm().getProducto().getNombre(),
                tarima.getPreTarima().getCantidadCajas(),
                DateUtils.formatear(tarima.getFechaAlmacen()),
                estado,
                ubicacion
        );
    }

    /**
     * Formatea la ubicación de un espacio
     */
    private String formatearUbicacion(Espacio espacio) {
        return String.format(
                "Almacén %d - Rack %d - Nivel %d - Lado %d - Posición %d",
                espacio.getRack().getAlmacen().getNumero(),
                espacio.getRack().getNumero(),
                espacio.getNivel(),
                espacio.getLado(),
                espacio.getPosicion()
        );
    }

    // DTO para respuesta
    public static class TarimaDTO {
        public Integer id;
        public String codigo;
        public String nombreProducto;
        public Integer cantidadCajas;
        public String fechaAlmacen;
        public String estado;
        public String ubicacion;

        public TarimaDTO(Integer id, String codigo, String nombreProducto, Integer cantidadCajas,
                         String fechaAlmacen, String estado, String ubicacion) {
            this.id = id;
            this.codigo = codigo;
            this.nombreProducto = nombreProducto;
            this.cantidadCajas = cantidadCajas;
            this.fechaAlmacen = fechaAlmacen;
            this.estado = estado;
            this.ubicacion = ubicacion;
        }

        // Getters
        public Integer getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getNombreProducto() { return nombreProducto; }
        public Integer getCantidadCajas() { return cantidadCajas; }
        public String getFechaAlmacen() { return fechaAlmacen; }
        public String getEstado() { return estado; }
        public String getUbicacion() { return ubicacion; }
    }
}