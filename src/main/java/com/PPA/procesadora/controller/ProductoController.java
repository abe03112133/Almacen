package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Producto;
import com.PPA.procesadora.service.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/productos")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Obtener todos los productos
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> obtenerTodos() {
        try {
            List<Producto> productos = productoService.obtenerTodos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productos);
            response.put("count", productos.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener productos", e);
        }
    }

    /**
     * Obtener solo productos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<Map<String, Object>> obtenerActivos() {
        try {
            List<Producto> productos = productoService.obtenerActivos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productos);
            response.put("count", productos.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener productos activos", e);
        }
    }

    /**
     * Obtener producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Producto> producto = productoService.obtenerPorId(id);
            Map<String, Object> response = new HashMap<>();

            if (producto.isPresent()) {
                response.put("success", true);
                response.put("data", producto.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener producto", e);
        }
    }

    /**
     * Obtener producto por código
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Map<String, Object>> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            Optional<Producto> producto = productoService.obtenerPorCodigo(codigo);
            Map<String, Object> response = new HashMap<>();

            if (producto.isPresent()) {
                response.put("success", true);
                response.put("data", producto.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener producto", e);
        }
    }

    /**
     * Crear nuevo producto
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crear(producto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado exitosamente");
            response.put("data", nuevoProducto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al crear producto", e);
        }
    }

    /**
     * Actualizar producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizar(id, producto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto actualizado exitosamente");
            response.put("data", productoActualizado);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al actualizar producto", e);
        }
    }

    /**
     * Eliminar producto (desactivar)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        try {
            productoService.eliminar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            return manejarError("Error al eliminar producto", e);
        }
    }

    /**
     * Eliminar permanentemente
     */
    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Map<String, Object>> eliminarPermanente(@PathVariable Integer id) {
        try {
            productoService.eliminarPermanente(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado permanentemente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            return manejarError("Error al eliminar producto", e);
        }
    }

    /**
     * Manejar errores
     */
    private ResponseEntity<Map<String, Object>> manejarError(String mensaje, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", mensaje);
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}