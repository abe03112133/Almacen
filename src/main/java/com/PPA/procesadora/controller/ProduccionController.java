package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Produccion;
import com.PPA.procesadora.service.ProduccionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/producciones")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProduccionController {

    private final ProduccionService produccionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodos() {
        try {
            List<Produccion> producciones = produccionService.obtenerTodos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", producciones);
            response.put("count", producciones.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener producciones", e);
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<Map<String, Object>> obtenerActivas() {
        try {
            List<Produccion> producciones = produccionService.obtenerActivos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", producciones);
            response.put("count", producciones.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener producciones activas", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Produccion> produccion = produccionService.obtenerPorId(id);
            Map<String, Object> response = new HashMap<>();

            if (produccion.isPresent()) {
                response.put("success", true);
                response.put("data", produccion.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Producción no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener producción", e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Produccion produccion) {
        try {
            Produccion nuevaProduccion = produccionService.crear(produccion);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producción creada exitosamente");
            response.put("data", nuevaProduccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return manejarError("Error al crear producción", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody Produccion produccion) {
        try {
            Produccion produccionActualizada = produccionService.actualizar(id, produccion);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producción actualizada exitosamente");
            response.put("data", produccionActualizada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al actualizar producción", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        try {
            produccionService.eliminarPermanente(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producción eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            return manejarError("Error al eliminar producción", e);
        }
    }

    private ResponseEntity<Map<String, Object>> manejarError(String mensaje, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", mensaje);
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}