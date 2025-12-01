package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Producto;
import com.PPA.procesadora.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Obtener todos los productos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    /**
     * Obtener todos los productos activos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorId(Integer id) {
        return productoRepository.findById(id);
    }

    /**
     * Obtener producto por código
     */
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    /**
     * Crear nuevo producto
     */
    public Producto crear(Producto producto) {
        validarCodigoUnico(producto.getCodigo(), null);
        validarDatosObligatorios(producto);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    /**
     * Actualizar producto existente
     */
    public Producto actualizar(Integer id, Producto productoActualizado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Validar código solo si cambió
        if (!producto.getCodigo().equals(productoActualizado.getCodigo())) {
            validarCodigoUnico(productoActualizado.getCodigo(), id);
        }

        validarDatosObligatorios(productoActualizado);

        producto.setCodigo(productoActualizado.getCodigo());
        producto.setNombre(productoActualizado.getNombre());
        producto.setAbreviacion(productoActualizado.getAbreviacion());
        producto.setPiezasPorCaja(productoActualizado.getPiezasPorCaja());
        producto.setColorDisplay(productoActualizado.getColorDisplay());
        producto.setActivo(productoActualizado.getActivo());

        return productoRepository.save(producto);
    }

    /**
     * Eliminar producto (borrado lógico)
     */
    public void eliminar(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    /**
     * Eliminar producto permanentemente
     */
    public void eliminarPermanente(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    /**
     * Validar que el código sea único
     */
    private void validarCodigoUnico(String codigo, Integer idExcluido) {
        Optional<Producto> existente = productoRepository.findByCodigo(codigo);
        if (existente.isPresent() && !existente.get().getId().equals(idExcluido)) {
            throw new RuntimeException("El código del producto ya existe: " + codigo);
        }
    }

    /**
     * Validar datos obligatorios
     */
    private void validarDatosObligatorios(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            throw new RuntimeException("El código del producto es obligatorio");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        if (producto.getPiezasPorCaja() == null || producto.getPiezasPorCaja() < 0) {
            throw new RuntimeException("Piezas por caja debe ser un número válido");
        }
    }
}