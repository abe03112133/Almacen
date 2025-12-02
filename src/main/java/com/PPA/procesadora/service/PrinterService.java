package com.PPA.procesadora.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrinterService {

    /**
     * Imprime un archivo PDF a una impresora en red usando protocolo Raw (puerto 9100)
     *
     * @param printerIp IP de la impresora (ej: 128.23.35.80)
     * @param pdfBytes Contenido del PDF en bytes
     * @param printJobName Nombre del trabajo de impresión
     * @return true si se envió correctamente, false si fallo
     */
    public boolean imprimirPDFaImpresora(String printerIp, byte[] pdfBytes, String printJobName) {
        return imprimirPDFaImpresora(printerIp, 9100, pdfBytes, printJobName);
    }

    /**
     * Imprime un archivo PDF a una impresora en red usando protocolo Raw
     *
     * @param printerIp IP de la impresora
     * @param puerto Puerto de la impresora (default 9100)
     * @param pdfBytes Contenido del PDF en bytes
     * @param printJobName Nombre del trabajo
     * @return true si se envió correctamente
     */
    public boolean imprimirPDFaImpresora(String printerIp, int puerto, byte[] pdfBytes, String printJobName) {
        Socket socket = null;
        OutputStream out = null;

        try {
            log.info("Conectando a impresora en {}:{}", printerIp, puerto);

            // Conectar a la impresora
            socket = new Socket(printerIp, puerto);
            socket.setSoTimeout(30000); // 30 segundos timeout

            out = socket.getOutputStream();

            // Enviar el contenido del PDF directamente
            out.write(pdfBytes);
            out.flush();

            log.info("PDF enviado exitosamente a impresora {}", printerIp);
            return true;

        } catch (IOException e) {
            log.error("Error al imprimir en {}: {}", printerIp, e.getMessage(), e);
            return false;

        } finally {
            // Cerrar conexión
            try {
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                log.error("Error al cerrar conexión: {}", e.getMessage());
            }
        }
    }

    /**
     * Imprime usando IPP (Internet Printing Protocol) - más compatible
     * Requiere CUPS instalado en la máquina servidor
     */
    public boolean imprimirPDFconIPP(String printerURL, byte[] pdfBytes, String printJobName) {
        try {
            log.info("Enviando trabajo de impresión a {}", printerURL);

            // IPP requiere más configuración, generalmente en servidor Linux/Mac
            // Por ahora usamos Raw que es más simple

            return false; // TODO: implementar IPP si es necesario

        } catch (Exception e) {
            log.error("Error en IPP: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Prueba conexión a la impresora
     */
    public boolean probarConexion(String printerIp) {
        return probarConexion(printerIp, 9100);
    }

    public boolean probarConexion(String printerIp, int puerto) {
        Socket socket = null;
        try {
            log.info("Probando conexión a {}:{}", printerIp, puerto);
            socket = new Socket(printerIp, puerto);
            socket.setSoTimeout(5000);
            log.info("Conexión exitosa a impresora {}", printerIp);
            return true;
        } catch (IOException e) {
            log.error("No se pudo conectar a impresora {}: {}", printerIp, e.getMessage());
            return false;
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                // Ignorar
            }
        }
    }
}