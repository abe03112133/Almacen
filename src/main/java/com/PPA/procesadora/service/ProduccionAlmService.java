package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Producto;
import com.PPA.procesadora.entity.Produccion;
import com.PPA.procesadora.entity.ProduccionAlm;
import com.PPA.procesadora.repository.ProductoRepository;
import com.PPA.procesadora.repository.ProduccionAlmRepository;
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
public class ProduccionAlmService {

    private final ProduccionAlmRepository produccionAlmRepository;
    private final ProduccionRepository produccionRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProduccionAlm> obtenerPorProduccion(Integer produccionId) {
        return produccionAlmRepository.findByProduccionId(produccionId);
    }

    @Transactional(readOnly = true)
    public Optional<ProduccionAlm> obtenerPorId(Integer id) {
        return produccionAlmRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ProduccionAlm> obtenerPorCodigo(String codigo) {
        return produccionAlmRepository.findByCodigo(codigo);
    }

    public ProduccionAlm crear(ProduccionAlm produccionAlm) {
        // Validar que la producción existe y está activa
        Produccion produccion = produccionRepository.findById(produccionAlm.getProduccion().getId())
                .orElseThrow(() -> new RuntimeException("Producción no encontrada"));

        if (!produccion.getActivo()) {
            throw new RuntimeException("No se puede crear ProduccionAlm en una Producción inactiva");
        }

        // Validar que el producto existe
        Producto producto = productoRepository.findById(produccionAlm.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getActivo()) {
            throw new RuntimeException("No se puede usar un Producto inactivo");
        }

        // Generar código automáticamente: abreviación del producto + ID
        String productoAbrev = producto.getAbreviacion() != null ?
                producto.getAbreviacion().toUpperCase() :
                producto.getCodigo().substring(0, Math.min(3, producto.getCodigo().length())).toUpperCase();

        produccionAlm.setEstatus("ACTIVA");
        ProduccionAlm guardada = produccionAlmRepository.save(produccionAlm);

        // Generar código después de guardar (cuando tenemos el ID)
        String codigoGenerado = productoAbrev + guardada.getId();
        guardada.setCodigo(codigoGenerado);

        return produccionAlmRepository.save(guardada);
    }

    public ProduccionAlm actualizar(Integer id, ProduccionAlm produccionAlmActualizada) {
        ProduccionAlm produccionAlm = produccionAlmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProduccionAlm no encontrada"));

        // Solo actualizar los campos que vienen en la request
        if (produccionAlmActualizada.getHoraFin() != null) {
            produccionAlm.setHoraFin(produccionAlmActualizada.getHoraFin());
        }
        if (produccionAlmActualizada.getCajasFinales() != null) {
            produccionAlm.setCajasFinales(produccionAlmActualizada.getCajasFinales());
        }
        if (produccionAlmActualizada.getEstatus() != null) {
            produccionAlm.setEstatus(produccionAlmActualizada.getEstatus());
        }

        return produccionAlmRepository.save(produccionAlm);
    }

    public void eliminar(Integer id) {
        ProduccionAlm produccionAlm = produccionAlmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProduccionAlm no encontrada"));
        produccionAlmRepository.deleteById(id);
    }

    private void validarCodigoUnico(String codigo, Integer idExcluido) {
        Optional<ProduccionAlm> existente = produccionAlmRepository.findByCodigo(codigo);
        if (existente.isPresent() && !existente.get().getId().equals(idExcluido)) {
            throw new RuntimeException("El código de ProduccionAlm ya existe: " + codigo);
        }
    }

    private void validarDatosObligatorios(ProduccionAlm produccionAlm) {
        if (produccionAlm.getCodigo() == null || produccionAlm.getCodigo().trim().isEmpty()) {
            throw new RuntimeException("El código es obligatorio");
        }
    }

    /**
     * Obtiene la ProduccionAlm activa actual
     */
    public Optional<ProduccionAlm> obtenerProduccionActiva() {
        return produccionAlmRepository.findByEstatus("ACTIVA");
    }

    /**
     * Finaliza una ProduccionAlm (acomodador inicia el proceso)
     * Cambia estado a PENDIENTE de aprobación supervisor
     */
    public ProduccionAlm finalizarProduccionAlm(Integer produccionAlmId) {
        ProduccionAlm produccionAlm = produccionAlmRepository.findById(produccionAlmId)
                .orElseThrow(() -> new RuntimeException("ProduccionAlm no encontrada"));

        if (!produccionAlm.getEstatus().equals("ACTIVA")) {
            throw new RuntimeException("Solo se pueden finalizar producciones ACTIVAS");
        }

        produccionAlm.setHoraFin(DateUtils.ahora());
        produccionAlm.setEstatus("PENDIENTE");

        return produccionAlmRepository.save(produccionAlm);
    }

    /**
     * Aprueba una ProduccionAlm (supervisor aprueba)
     */
    public ProduccionAlm aprobarProduccionAlm(Integer produccionAlmId) {
        ProduccionAlm produccionAlm = produccionAlmRepository.findById(produccionAlmId)
                .orElseThrow(() -> new RuntimeException("ProduccionAlm no encontrada"));

        if (!produccionAlm.getEstatus().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden aprobar producciones PENDIENTE");
        }

        produccionAlm.setEstatus("TERMINADA");
        return produccionAlmRepository.save(produccionAlm);
    }

    /**
     * Rechaza una ProduccionAlm (supervisor rechaza)
     * Vuelve a estado ACTIVA
     */
    public ProduccionAlm rechazarProduccionAlm(Integer produccionAlmId) {
        ProduccionAlm produccionAlm = produccionAlmRepository.findById(produccionAlmId)
                .orElseThrow(() -> new RuntimeException("ProduccionAlm no encontrada"));

        if (!produccionAlm.getEstatus().equals("PENDIENTE")) {
            throw new RuntimeException("Solo se pueden rechazar producciones PENDIENTE");
        }

        produccionAlm.setEstatus("ACTIVA");
        produccionAlm.setHoraFin(null); // Limpiar hora fin
        return produccionAlmRepository.save(produccionAlm);
    }
}