package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Insumo;
import com.PPA.procesadora.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsumoService {

    private final InsumoRepository insumoRepository;

    /**
     * Obtiene todos los insumos
     */
    @Transactional(readOnly = true)
    public List<Insumo> obtenerTodos() {
        return insumoRepository.findAll();
    }

    /**
     * Obtiene un insumo por ID
     */
    @Transactional(readOnly = true)
    public Insumo obtenerPorId(Integer id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + id));
    }

    /**
     * Obtiene un insumo por código
     */
    @Transactional(readOnly = true)
    public Insumo obtenerPorCodigo(String codigo) {
        return insumoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + codigo));
    }

    /**
     * Crea un nuevo insumo
     */
    @Transactional
    public Insumo crear(String codigo, String descripcion) {
        // Validar que no exista ya
        if (insumoRepository.findByCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("El código de insumo ya existe: " + codigo);
        }

        Insumo insumo = new Insumo();
        insumo.setCodigo(codigo);
        insumo.setDescripcion(descripcion);

        Insumo guardado = insumoRepository.save(insumo);
        log.info("Insumo creado: {} - {}", codigo, descripcion);

        return guardado;
    }

    /**
     * Actualiza un insumo
     */
    @Transactional
    public Insumo actualizar(Integer id, String codigo, String descripcion) {
        Insumo insumo = obtenerPorId(id);

        // Si cambia el código, validar que no exista
        if (!insumo.getCodigo().equals(codigo)) {
            if (insumoRepository.findByCodigo(codigo).isPresent()) {
                throw new IllegalArgumentException("El código de insumo ya existe: " + codigo);
            }
        }

        insumo.setCodigo(codigo);
        insumo.setDescripcion(descripcion);

        Insumo guardado = insumoRepository.save(insumo);
        log.info("Insumo actualizado: {} - {}", codigo, descripcion);

        return guardado;
    }

    /**
     * Elimina un insumo
     */
    @Transactional
    public void eliminar(Integer id) {
        Insumo insumo = obtenerPorId(id);
        insumoRepository.delete(insumo);
        log.info("Insumo eliminado: {}", insumo.getCodigo());
    }
}