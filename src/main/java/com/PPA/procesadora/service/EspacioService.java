package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Espacio;
import com.PPA.procesadora.entity.Rack;
import com.PPA.procesadora.repository.EspacioRepository;
import com.PPA.procesadora.repository.RackRepository;
import com.PPA.procesadora.util.Constants;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final RackRepository rackRepository;

    /**
     * Parsea un código QR y busca espacios disponibles en ese nivel/lado
     */
    @Transactional(readOnly = true)
    public List<Espacio> findEspaciosDisponiblesByCodigoQr(String codigoQr) {
        // Si el código no tiene prefijo, agregarlo
        if (!codigoQr.startsWith(Constants.PREFIJO_CODIGO_QR)) {
            codigoQr = Constants.PREFIJO_CODIGO_QR + codigoQr;
            log.info("Código QR completado: {}", codigoQr);
        }

        // Parsear código QR: +C1PACON300131 → prefijo=30, numero=01, nivel=3, lado=1
        if (!codigoQr.startsWith(Constants.PREFIJO_CODIGO_QR)) {
            throw new IllegalArgumentException("Código QR inválido");
        }

        String codigoSinPrefijo = codigoQr.substring(Constants.PREFIJO_CODIGO_QR.length());

        String prefijo = codigoSinPrefijo.substring(0, 2);      // 30
        Integer numero = Integer.parseInt(codigoSinPrefijo.substring(2, 4));  // 01
        Integer nivel = Integer.parseInt(codigoSinPrefijo.substring(4, 5));   // 3
        Integer lado = Integer.parseInt(codigoSinPrefijo.substring(5, 6));    // 1

        Optional<Rack> rackOpt = rackRepository.findByPrefijoAndNumero(prefijo, numero);
        if (rackOpt.isEmpty()) {
            throw new IllegalArgumentException("Rack no encontrado");
        }

        Rack rack = rackOpt.get();
        return espacioRepository.findByRackAndNivelAndLadoAndEstadoOrderByPosicion(
                rack, nivel, lado, Constants.ESTADO_DISPONIBLE
        );
    }

    /**
     * Obtiene el espacio PEPS y verifica disponibilidad
     * Retorna el espacio a asignar y espacios restantes después de asignar
     */
    @Transactional(readOnly = true)
    public EspacioInfoDTO getEspacioInfo(String codigoQr) {
        // Si el código no tiene prefijo, agregarlo
        if (!codigoQr.startsWith(Constants.PREFIJO_CODIGO_QR)) {
            codigoQr = Constants.PREFIJO_CODIGO_QR + codigoQr;
            log.info("Código QR completado: {}", codigoQr);
        }

        if (!codigoQr.startsWith(Constants.PREFIJO_CODIGO_QR)) {
            throw new IllegalArgumentException("Código QR inválido");
        }

        String codigoSinPrefijo = codigoQr.substring(Constants.PREFIJO_CODIGO_QR.length());
        String prefijo = codigoSinPrefijo.substring(0, 2);
        Integer numero = Integer.parseInt(codigoSinPrefijo.substring(2, 4));
        Integer nivel = Integer.parseInt(codigoSinPrefijo.substring(4, 5));
        Integer lado = Integer.parseInt(codigoSinPrefijo.substring(5, 6));

        Optional<Rack> rackOpt = rackRepository.findByPrefijoAndNumero(prefijo, numero);
        if (rackOpt.isEmpty()) {
            throw new IllegalArgumentException("Rack no encontrado: " + prefijo + numero);
        }

        Rack rack = rackOpt.get();

        // Buscar espacios disponibles en este rack/nivel/lado (ordenados por posición PEPS)
        List<Espacio> disponibles = espacioRepository.findByRackAndNivelAndLadoAndEstadoOrderByPosicion(
                rack, nivel, lado, Constants.ESTADO_DISPONIBLE
        );

        EspacioInfoDTO info = new EspacioInfoDTO();
        info.setAlmacenNumero(rack.getAlmacen().getNumero());
        info.setRackNumero(rack.getNumero());
        info.setNivel(nivel);
        info.setLado(lado);
        info.setTipoRack(rack.getTipoRack().getCodigo());

        if (disponibles.isEmpty()) {
            // No hay disponibles
            info.setExitoso(false);
            info.setEspaciosDisponibles(0);
            info.setSugerencia(sugerirProximoNivel(rack, nivel));
            log.warn("Sin espacios - Rack: {}, Nivel: {}, Lado: {}", prefijo + numero, nivel, lado);
        } else {
            // Hay disponibles - Seleccionar el PRIMERO (PEPS)
            Espacio espacioPEPS = disponibles.get(0);
            info.setExitoso(true);
            info.setPosicionDisponible(espacioPEPS.getId());

            // Calcular espacios restantes DESPUÉS de asignar este
            int espaciosRestantes = disponibles.size() - 1;
            info.setEspaciosDisponibles(espaciosRestantes);

            log.info("PEPS asignado - Rack: {}, Nivel: {}, Lado: {}, Espacios restantes: {}",
                    prefijo + numero, nivel, lado, espaciosRestantes);
        }

        return info;
    }

    /**
     * Sugiere el próximo nivel disponible según el tipo de rack (FIFO/FILO)
     */
    private String sugerirProximoNivel(Rack rack, Integer nivelActual) {
        String tipoRack = rack.getTipoRack().getCodigo();
        Integer proximoNivel = null;

        if ("DINAMICO".equals(tipoRack) || "TUNEL".equals(tipoRack)) {
            // FIFO: buscar siguiente nivel hacia arriba
            for (int n = nivelActual + 1; n <= rack.getNivelesActivos(); n++) {
                Long disponibles = espacioRepository.countByRackAndNivelAndLadoAndEstado(
                        rack, n, 1, Constants.ESTADO_DISPONIBLE
                );
                if (disponibles > 0) {
                    proximoNivel = n;
                    break;
                }
            }
        } else if ("REVERSIBLE".equals(tipoRack)) {
            // FILO: buscar siguiente nivel hacia abajo
            for (int n = nivelActual - 1; n >= 1; n--) {
                Long disponibles = espacioRepository.countByRackAndNivelAndLadoAndEstado(
                        rack, n, 1, Constants.ESTADO_DISPONIBLE
                );
                if (disponibles > 0) {
                    proximoNivel = n;
                    break;
                }
            }
        }

        if (proximoNivel != null) {
            return String.format("Sugerencia: Nivel %d disponible en el mismo rack", proximoNivel);
        }
        return "No hay niveles disponibles en este rack";
    }

    /**
     * Guarda una tarima en un espacio específico
     */
    @Transactional
    public void guardarTarimaEnEspacio(Integer espacioId, Integer tarimaId) {
        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        if (!Constants.ESTADO_DISPONIBLE.equals(espacio.getEstado())) {
            throw new IllegalArgumentException("El espacio no está disponible");
        }

        espacio.setEstado(Constants.ESTADO_OCUPADO);
        espacio.setFechaOcupacion(DateUtils.ahora());
        espacioRepository.save(espacio);
    }

    // DTO para respuesta de info de espacio
    public static class EspacioInfoDTO {
        public Integer almacenNumero;
        public Integer rackNumero;
        public Integer nivel;
        public Integer lado;
        public String tipoRack;
        public Integer espaciosDisponibles;
        public Integer posicionDisponible;
        public String sugerencia;
        public Boolean exitoso;

        // Getters y Setters
        public Integer getAlmacenNumero() { return almacenNumero; }
        public void setAlmacenNumero(Integer almacenNumero) { this.almacenNumero = almacenNumero; }

        public Integer getRackNumero() { return rackNumero; }
        public void setRackNumero(Integer rackNumero) { this.rackNumero = rackNumero; }

        public Integer getNivel() { return nivel; }
        public void setNivel(Integer nivel) { this.nivel = nivel; }

        public Integer getLado() { return lado; }
        public void setLado(Integer lado) { this.lado = lado; }

        public String getTipoRack() { return tipoRack; }
        public void setTipoRack(String tipoRack) { this.tipoRack = tipoRack; }

        public Integer getEspaciosDisponibles() { return espaciosDisponibles; }
        public void setEspaciosDisponibles(Integer espaciosDisponibles) { this.espaciosDisponibles = espaciosDisponibles; }

        public Integer getPosicionDisponible() { return posicionDisponible; }
        public void setPosicionDisponible(Integer posicionDisponible) { this.posicionDisponible = posicionDisponible; }

        public String getSugerencia() { return sugerencia; }
        public void setSugerencia(String sugerencia) { this.sugerencia = sugerencia; }

        public Boolean getExitoso() { return exitoso; }
        public void setExitoso(Boolean exitoso) { this.exitoso = exitoso; }
    }
}