package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Destino;
import com.PPA.procesadora.service.DestinoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/destino")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DestinoController {

    private final DestinoService destinoService;

    /**
     * Obtener todos los destinos
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> obtenerTodos() {
        try {
            List<Destino> destinos = destinoService.obtenerTodos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", destinos);
            response.put("count", destinos.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener destinos", e);
        }
    }

    /**
     * Obtener solo destinos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<Map<String, Object>> obtenerActivos() {
        try {
            List<Destino> destinos = destinoService.obtenerActivos();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", destinos);
            response.put("count", destinos.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return manejarError("Error al obtener destinos activos", e);
        }
    }

    /**
     * Obtener destino por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        try {
            Optional<Destino> destino = destinoService.obtenerPorId(id);
            Map<String, Object> response = new HashMap<>();

            if (destino.isPresent()) {
                response.put("success", true);
                response.put("data", destino.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Destino no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener destino", e);
        }
    }

    /**
     * Obtener destino por código
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Map<String, Object>> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            Optional<Destino> destino = destinoService.obtenerPorCodigo(codigo);
            Map<String, Object> response = new HashMap<>();

            if (destino.isPresent()) {
                response.put("success", true);
                response.put("data", destino.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Destino no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            return manejarError("Error al obtener destino", e);
        }
    }

    /**
     * Crear nuevo destino
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Destino destino) {
        try {
            Destino nuevoDestino = destinoService.crear(destino);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Destino creado exitosamente");
            response.put("data", nuevoDestino);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al crear destino", e);
        }
    }

    /**
     * Actualizar destino existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody Destino destino) {
        try {
            Destino destinoActualizado = destinoService.actualizar(id, destino);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Destino actualizado exitosamente");
            response.put("data", destinoActualizado);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return manejarError("Error al actualizar destino", e);
        }
    }

    /**
     * Eliminar destino (desactivar)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        try {
            destinoService.eliminar(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Destino eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            return manejarError("Error al eliminar destino", e);
        }
    }

    /**
     * Manejo centralizado de errores
     */
    private ResponseEntity<Map<String, Object>> manejarError(String mensaje, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", mensaje);
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
