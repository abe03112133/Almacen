package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Produccion;
import com.PPA.procesadora.repository.ProduccionRepository;
import com.PPA.procesadora.util.DateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ProduccionService {

    private final ProduccionRepository produccionRepository;

    @Transactional(readOnly = true)
    public List<Produccion> obtenerTodos() {
        return produccionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Produccion> obtenerActivos() {
        return produccionRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Produccion> obtenerPorId(Integer id) {
        return produccionRepository.findById(id);
    }

    public Produccion crear(Produccion produccion) {
        if (produccion.getFecha() == null) {
            produccion.setFecha(DateUtils.ahora());
        }
        produccion.setActivo(true);
        return produccionRepository.save(produccion);
    }

    public Produccion actualizar(Integer id, Produccion produccionActualizada) {
        Produccion produccion = produccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producción no encontrada con ID: " + id));

        produccion.setActivo(produccionActualizada.getActivo());

        return produccionRepository.save(produccion);
    }

    public void eliminar(Integer id) {
        Produccion produccion = produccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producción no encontrada con ID: " + id));
        produccion.setActivo(false);
        produccionRepository.save(produccion);
    }

    public void eliminarPermanente(Integer id) {
        if (!produccionRepository.existsById(id)) {
            throw new RuntimeException("Producción no encontrada con ID: " + id);
        }
        produccionRepository.deleteById(id);
    }
}