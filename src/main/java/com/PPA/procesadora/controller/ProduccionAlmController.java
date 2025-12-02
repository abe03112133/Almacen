package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.ProduccionAlm;
import com.PPA.procesadora.service.ProduccionAlmService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/produccion-alm")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProduccionAlmController {

    private final ProduccionAlmService produccionAlmService;

    @GetMapping("/produccion/{produccionId}")
    public ResponseEntity<Map<String, Object>> obtenerPorProduccion(@PathVariable Integer produccionId) {
        try {
            List<ProduccionAlm> produccionesAlm = produccionAlmService.obtenerPorProduccion(produccionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", produccionesAlm);
            response.put("count", produccionesAlm.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener producciones de almacén", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<ProduccionAlm> produccionAlm = produccionAlmService.obtenerPorId(id);
            Map<String, Object> response = new HashMap<>();

            if (produccionAlm.isPresent()) {
                response.put("success", true);
                response.put("data", produccionAlm.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "ProduccionAlm no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener Produccion", e);
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Map<String, Object>> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            Optional<ProduccionAlm> produccionAlm = produccionAlmService.obtenerPorCodigo(codigo);
            Map<String, Object> response = new HashMap<>();

            if (produccionAlm.isPresent()) {
                response.put("success", true);
                response.put("data", produccionAlm.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "ProduccionAlm no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener Produccion", e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody ProduccionAlm produccionAlm) {
        try {
            ProduccionAlm nuevaProduccionAlm = produccionAlmService.crear(produccionAlm);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produccion de Almacen creada exitosamente");
            response.put("data", nuevaProduccionAlm);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al crear", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody ProduccionAlm produccionAlm) {
        try {
            ProduccionAlm produccionAlmActualizada = produccionAlmService.actualizar(id, produccionAlm);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produccion actualizada exitosamente");
            response.put("data", produccionAlmActualizada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al actualizar", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        try {
            produccionAlmService.eliminar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ProduccionAlm eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            return manejarError("Error al eliminar ProduccionAlm", e);
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