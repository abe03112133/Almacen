package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Maquina;
import com.PPA.procesadora.entity.Insumo;
import com.PPA.procesadora.repository.MaquinaRepository;
import com.PPA.procesadora.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaquinaService {

    private final MaquinaRepository maquinaRepository;
    private final InsumoRepository insumoRepository;

    /**
     * Obtiene todas las máquinas
     */
    @Transactional(readOnly = true)
    public List<Maquina> obtenerTodas() {
        return maquinaRepository.findAll();
    }

    /**
     * Obtiene todas las máquinas activas
     */
    @Transactional(readOnly = true)
    public List<Maquina> obtenerActivas() {
        return maquinaRepository.findByActivoTrue();
    }

    /**
     * Obtiene una máquina por ID
     */
    @Transactional(readOnly = true)
    public Maquina obtenerPorId(Integer id) {
        return maquinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + id));
    }

    /**
     * Obtiene una máquina por código (ej: M-C300)
     */
    @Transactional(readOnly = true)
    public Maquina obtenerPorCodigo(String codigo) {
        return maquinaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Máquina no encontrada: " + codigo));
    }

    /**
     * Obtiene máquinas por insumo
     */
    @Transactional(readOnly = true)
    public List<Maquina> obtenerPorInsumo(Integer insumoId) {
        return maquinaRepository.findByInsumoId(insumoId);
    }

    /**
     * Crea una nueva máquina
     * El código debe incluir el prefijo M-
     */
    @Transactional
    public Maquina crear(String codigo, String nombre, Integer insumoId, Boolean activo) {
        // Validar código
        if (!codigo.startsWith("M-")) {
            codigo = "M-" + codigo;
        }

        // Validar que no exista ya
        if (maquinaRepository.findByCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("El código de máquina ya existe: " + codigo);
        }

        // Obtener insumo
        Insumo insumo = insumoRepository.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + insumoId));

        Maquina maquina = new Maquina();
        maquina.setCodigo(codigo);
        maquina.setNombre(nombre);
        maquina.setInsumo(insumo);
        maquina.setActivo(activo != null ? activo : true);

        Maquina guardada = maquinaRepository.save(maquina);
        log.info("Máquina creada: {} - {} (Insumo: {})", codigo, nombre, insumo.getCodigo());

        return guardada;
    }

    /**
     * Actualiza una máquina
     */
    @Transactional
    public Maquina actualizar(Integer id, String nombre, Integer insumoId, Boolean activo) {
        Maquina maquina = obtenerPorId(id);

        // Obtener nuevo insumo
        Insumo insumo = insumoRepository.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + insumoId));

        maquina.setNombre(nombre);
        maquina.setInsumo(insumo);
        maquina.setActivo(activo != null ? activo : true);

        Maquina guardada = maquinaRepository.save(maquina);
        log.info("Máquina actualizada: {} - {} (Insumo: {})", maquina.getCodigo(), nombre, insumo.getCodigo());

        return guardada;
    }

    /**
     * Elimina una máquina
     */
    @Transactional
    public void eliminar(Integer id) {
        Maquina maquina = obtenerPorId(id);
        maquinaRepository.delete(maquina);
        log.info("Máquina eliminada: {}", maquina.getCodigo());
    }

    /**
     * Cambia el estado de una máquina
     */
    @Transactional
    public Maquina cambiarEstado(Integer id, Boolean activo) {
        Maquina maquina = obtenerPorId(id);
        maquina.setActivo(activo);
        Maquina guardada = maquinaRepository.save(maquina);
        log.info("Estado de máquina actualizado: {} - {}", maquina.getCodigo(), activo ? "Activa" : "Inactiva");
        return guardada;
    }
}