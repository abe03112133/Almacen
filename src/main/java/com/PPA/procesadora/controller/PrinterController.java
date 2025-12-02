package com.PPA.procesadora.controller;

import com.PPA.procesadora.service.PrinterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/printer")
@RequiredArgsConstructor
public class PrinterController {

    private final PrinterService printerService;

    /**
     * Prueba conexión a la impresora
     * GET /api/printer/test?ip=128.23.35.80&puerto=9100
     */
    @GetMapping("/pdf/test")
    public ResponseEntity<?> probarConexion(
            @RequestParam String ip,
            @RequestParam(defaultValue = "9100") int puerto) {

        boolean conectado = printerService.probarConexion(ip, puerto);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("conectado", conectado);
        respuesta.put("mensaje", conectado
                ? "Conexión exitosa a impresora " + ip + ":" + puerto
                : "No se pudo conectar a impresora " + ip + ":" + puerto);
        respuesta.put("ip_impresora", ip);
        respuesta.put("puerto_impresora", puerto);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Imprime un PDF
     * POST /api/printer/print
     * Body: { "ip": "128.23.35.80", "puerto": 9100, "pdfBase64": "...", "jobName": "PreTarima_001" }
     */
    @PostMapping("/print")
    public ResponseEntity<?> imprimirPDF(
            @RequestParam String ip,
            @RequestParam(defaultValue = "9100") int puerto,
            @RequestBody byte[] pdfBytes,
            @RequestParam String jobName) {

        boolean impreso = printerService.imprimirPDFaImpresora(ip, puerto, pdfBytes, jobName);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("impreso", impreso);
        respuesta.put("mensaje", impreso
                ? "PDF enviado a impresora " + ip
                : "Error al enviar PDF a impresora");
        respuesta.put("jobName_enviado", jobName);

        return ResponseEntity.ok(respuesta);
    }
}