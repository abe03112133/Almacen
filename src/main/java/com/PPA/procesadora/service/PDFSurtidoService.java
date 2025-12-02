package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.*;
import com.PPA.procesadora.repository.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SERVICIO: PDFGeneratorService
 *
 * Genera PDF descargable con picklist para surtidor.
 *
 * Contenido del PDF:
 * - Header: Información del SurtidoDiario y SurtidoProducto
 * - Tabla: Rack, Nivel, Posición, Cantidad Tarimas, Espacio Observaciones
 * - Footer: Instrucciones para surtidor
 *
 * Características:
 * - NO impresión automática (el usuario descarga y decide)
 * - Optimizado para papel A4
 * - Código QR origen para referencia rápida
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PDFSurtidoService {

    private final SurtidoDiarioRepository surtidoDiarioRepository;
    private final SurtidoProductoRepository surtidoProductoRepository;
    private final DetalleSurtidoRepository detalleSurtidoRepository;

    /**
     * Genera picklist PDF para un SurtidoProducto
     */
    public byte[] generarPicklistPDF(Integer surtidoProductoId) throws Exception {
        SurtidoProducto sp = surtidoProductoRepository.findById(surtidoProductoId)
                .orElseThrow(() -> new RuntimeException("SurtidoProducto no encontrado"));

        SurtidoDiario sd = sp.getSurtidoDiario();
        List<DetalleSurtido> detalles = detalleSurtidoRepository.findBySurtidoProducto(sp);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontSmall = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ===== HEADER =====
        Paragraph titulo = new Paragraph("PICKLIST DE SURTIDO")
                .setFont(fontBold)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        // Información del surtido
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String infoHeader = String.format(
                "Fecha: %s | Producto: %s | Destino: %s | Cantidad Solicitada: %d | Cantidad Asignada: %d",
                sd.getFecha().format(formatter),
                sp.getProducto().getNombre(),
                sp.getDestino().getNombre(),
                sp.getCantidadSolicitada(),
                sp.getCantidadAsignada()
        );

        Paragraph info = new Paragraph(infoHeader)
                .setFont(fontNormal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(15);
        document.add(info);

        // ===== TABLA =====
        float[] columnWidths = {60, 60, 60, 120, 100};
        Table table = new Table(columnWidths);

        // Headers
        String[] headers = {"Rack", "Nivel", "Pos", "Cant", "Código QR Origen"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setFont(fontBold))
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        }

        // Datos
        for (DetalleSurtido detalle : detalles) {
            // Rack
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detalle.getNumeroRackOrigen()))
                            .setFont(fontSmall).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER));

            // Nivel
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detalle.getNivelOrigen()))
                            .setFont(fontSmall).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER));

            // Posición
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detalle.getPosicionOrigen()))
                            .setFont(fontSmall).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER));

            // Cantidad
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detalle.getCantidadTarimas()))
                            .setFont(fontSmall).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER));

            // Código QR
            table.addCell(new Cell()
                    .add(new Paragraph(detalle.getCodigoQrOrigen() != null ?
                            detalle.getCodigoQrOrigen() : "N/A")
                            .setFont(fontSmall).setFontSize(9))
                    .setTextAlignment(TextAlignment.LEFT));
        }

        document.add(table);

        // ===== FOOTER =====
        Paragraph footer = new Paragraph(
                "\n\nINSTRUCCIONES:\n" +
                        "1. Baje las tarimas según este picklist\n" +
                        "2. Si no encuentra una tarima, anótelo en el picklist\n" +
                        "3. Si baja tarimas extra, anótelas también\n" +
                        "4. Devuelva el picklist anotado al supervisor"
        )
                .setFont(fontNormal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20);
        document.add(footer);

        document.close();

        log.info("✓ PDF generado: SurtidoProducto={}, detalles={}", surtidoProductoId, detalles.size());
        return baos.toByteArray();
    }

    /**
     * Genera resumen PDF de un SurtidoDiario completo (todos los productos)
     */
    public byte[] generarResumenSurtidoDiarioPDF(Integer surtidoDiarioId) throws Exception {
        SurtidoDiario sd = surtidoDiarioRepository.findById(surtidoDiarioId)
                .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

        List<SurtidoProducto> productos = surtidoProductoRepository.findBySurtidoDiario(sd);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Título
        Paragraph titulo = new Paragraph("RESUMEN SURTIDO DIARIO")
                .setFont(fontBold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
        document.add(titulo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String infoGeneral = String.format(
                "Fecha: %s | Estado: %s | Supervisor: %s",
                sd.getFecha().format(formatter),
                sd.getEstado(),
                sd.getSupervisorAsignado() != null ? sd.getSupervisorAsignado().getNombreCompleto() : "N/A"
        );

        Paragraph info = new Paragraph(infoGeneral)
                .setFont(fontNormal)
                .setFontSize(11)
                .setMarginBottom(20);
        document.add(info);

        // Tabla resumen productos
        float[] columnWidths = {100, 100, 80, 80, 80, 80};
        Table table = new Table(columnWidths);

        String[] headers = {"Producto", "Destino", "Solicitado", "Asignado", "Surtido", "Faltante"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setFont(fontBold))
                    .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        }

        int totalSolicitado = 0;
        int totalAsignado = 0;
        int totalSurtido = 0;

        for (SurtidoProducto sp : productos) {
            table.addCell(sp.getProducto().getNombre());
            table.addCell(sp.getDestino().getNombre());
            table.addCell(String.valueOf(sp.getCantidadSolicitada()));
            table.addCell(String.valueOf(sp.getCantidadAsignada()));
            table.addCell(String.valueOf(sp.getCantidadSurtida()));
            table.addCell(String.valueOf(sp.cantidadFaltante()));

            totalSolicitado += sp.getCantidadSolicitada();
            totalAsignado += sp.getCantidadAsignada();
            totalSurtido += sp.getCantidadSurtida();
        }

        // Totales
        Cell cellTotal = new Cell()
                .add(new Paragraph("TOTAL").setFont(fontBold))
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
        table.addCell(cellTotal);
        table.addCell("");
        table.addCell(String.valueOf(totalSolicitado));
        table.addCell(String.valueOf(totalAsignado));
        table.addCell(String.valueOf(totalSurtido));
        table.addCell(String.valueOf(totalSolicitado - totalSurtido));

        document.add(table);
        document.close();

        log.info("✓ Resumen PDF generado: SurtidoDiario={}, productos={}", surtidoDiarioId, productos.size());
        return baos.toByteArray();
    }
}