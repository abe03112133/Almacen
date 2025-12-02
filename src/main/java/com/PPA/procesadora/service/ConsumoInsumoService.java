package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.ConsumoInsumo;
import com.PPA.procesadora.entity.Maquina;
import com.PPA.procesadora.entity.Insumo;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.entity.ProduccionAlm;
import com.PPA.procesadora.repository.ConsumoInsumoRepository;
import com.PPA.procesadora.repository.MaquinaRepository;
import com.PPA.procesadora.repository.InsumoRepository;
import com.PPA.procesadora.repository.ProduccionAlmRepository;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumoInsumoService {

    private final ConsumoInsumoRepository consumoInsumoRepository;
    private final MaquinaRepository maquinaRepository;
    private final InsumoRepository insumoRepository;
    private final ProduccionAlmRepository produccionAlmRepository;

    /**
     * Registra el consumo de un insumo por máquina escaneada
     */
    @Transactional
    public ConsumoInsumo registrar(Integer maquinaId, Integer insumoId, Integer produccionAlmId, Usuario usuario) {
        // Obtener máquina
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + maquinaId));

        // Obtener insumo
        Insumo insumo = insumoRepository.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + insumoId));

        // Validar que el insumo sea el correcto de la máquina
        if (!maquina.getInsumo().getId().equals(insumoId)) {
            throw new IllegalArgumentException("El insumo no coincide con el asignado a la máquina");
        }

        // Crear registro de consumo
        ConsumoInsumo consumo = new ConsumoInsumo();
        consumo.setMaquina(maquina);
        consumo.setInsumo(insumo);
        consumo.setFechaHora(DateUtils.ahora());
        consumo.setUsuario(usuario);

        // Obtener producción si se proporciona
        if (produccionAlmId != null) {
            ProduccionAlm produccionAlm = produccionAlmRepository.findById(produccionAlmId)
                    .orElseThrow(() -> new IllegalArgumentException("Producción no encontrada: " + produccionAlmId));
            consumo.setProduccionAlm(produccionAlm);
        }

        ConsumoInsumo guardado = consumoInsumoRepository.save(consumo);
        log.info("Consumo registrado - Máquina: {}, Insumo: {}, Hora: {}",
                maquina.getCodigo(), insumo.getCodigo(), DateUtils.formatear(DateUtils.ahora()));

        return guardado;
    }

    /**
     * Obtiene el historial de consumo de una máquina
     */
    @Transactional(readOnly = true)
    public List<ConsumoInsumo> obtenerHistorialMaquina(Integer maquinaId) {
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + maquinaId));

        return consumoInsumoRepository.findByMaquinaOrderByFechaHoraDesc(maquina);
    }

    /**
     * Obtiene consumos en un rango de fecha
     */
    @Transactional(readOnly = true)
    public List<ConsumoInsumo> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return consumoInsumoRepository.findByFechaHoraBetween(inicio, fin);
    }

    /**
     * Obtiene consumos de una máquina en un rango de fecha
     */
    @Transactional(readOnly = true)
    public List<ConsumoInsumo> obtenerPorMaquinaYFecha(Integer maquinaId, LocalDateTime inicio, LocalDateTime fin) {
        // Validar máquina existe
        maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + maquinaId));

        return consumoInsumoRepository.findByMaquinaAndFecha(maquinaId, inicio, fin);
    }

    /**
     * Obtiene consumos de una producción
     */
    @Transactional(readOnly = true)
    public List<ConsumoInsumo> obtenerPorProduccion(Integer produccionAlmId) {
        return consumoInsumoRepository.findByProduccionAlm(produccionAlmId);
    }

    /**
     * Obtiene el último consumo registrado de una máquina
     */
    @Transactional(readOnly = true)
    public ConsumoInsumo obtenerUltimoConsumo(Integer maquinaId) {
        Maquina maquina = maquinaRepository.findById(maquinaId)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + maquinaId));

        List<ConsumoInsumo> consumos = consumoInsumoRepository.findByMaquinaOrderByFechaHoraDesc(maquina);

        if (consumos.isEmpty()) {
            return null;
        }

        return consumos.get(0);
    }
}