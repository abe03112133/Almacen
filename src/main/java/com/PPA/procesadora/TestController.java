package com.PPA.procesadora;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "PPA Procesadora - Almacén está corriendo");
        response.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, String> response = new HashMap<>();
        response.put("aplicacion", "PPA Procesadora");
        response.put("version", "1.0.0");
        response.put("descripcion", "Sistema de Control de Almacén Dinámico");
        return ResponseEntity.ok(response);
    }

}