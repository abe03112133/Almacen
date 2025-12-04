package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.*;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PDFGeneratorService {

    /**
     * Genera PDF de PreTarima con código de barras
     * Nota: No incluye comandos de bandeja. La bandeja se configura como DEFAULT en la impresora
     */
    public byte[] generarPDFPreTarima(PreTarima preTarima, Integer numeroBandeja) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.LETTER);

            Document doc = new Document(pdfDoc);
            doc.setMargins(30, 10, 10, 10);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont smallFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Producto producto = preTarima.getProduccionAlm().getProducto();
            Produccion produccion = preTarima.getProduccion();
            EstadoProducto estadoProducto = preTarima.getEstadoProducto();

            // Código de producto
            Paragraph codigoProducto = new Paragraph(producto.getCodigo())
                    .setFont(boldFont)
                    .setFontSize(50)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8);
            doc.add(codigoProducto);

            // Nombre del producto
            Paragraph nombre = new Paragraph(producto.getNombre())
                    .setFont(boldFont)
                    .setFontSize(50)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            doc.add(nombre);

            // Cajas y piezas
            String cajasPiezas = preTarima.getCantidadCajas() + " CAJAS/" + producto.getPiezasPorCaja() + "PZ";
            Paragraph cajas = new Paragraph(cajasPiezas)
                    .setFont(regularFont)
                    .setFontSize(26)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8);
            doc.add(cajas);

            // Lote (fecha de producción)
            String lote = "LOTE:                " + produccion.getFecha().format(DateTimeFormatter.ofPattern("MMM dd yyyy")).toUpperCase();
            Paragraph lotePara = new Paragraph(lote)
                    .setFont(smallFont)
                    .setFontSize(26)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4);
            doc.add(lotePara);

            // Caducidad (6 meses después)
            LocalDateTime caducidad = produccion.getFecha().plusMonths(6);
            String fechaCaducidad = caducidad.format(DateTimeFormatter.ofPattern("MMM dd yyyy")).toUpperCase();
            Paragraph caducidadPara = new Paragraph("CADUCIDAD:          " + fechaCaducidad)
                    .setFont(smallFont)
                    .setFontSize(26)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(25);
            doc.add(caducidadPara);

            // Código de barras
            try {
                Image codigoBarras = generarCodigoBarras(preTarima.getCodigo());
                if (codigoBarras != null) {
                    codigoBarras.setWidth(260);
                    codigoBarras.setHeight(70);
                    Paragraph barrasPara = new Paragraph()
                            .add(codigoBarras)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(6);
                    doc.add(barrasPara);
                }
            } catch (Exception e) {
                log.warn("Error generando código de barras: {}", e.getMessage());
            }

            // Código de PreTarima
            Paragraph codigoPreTarima = new Paragraph(preTarima.getCodigo())
                    .setFont(boldFont)
                    .setFontSize(28)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4);
            doc.add(codigoPreTarima);

            // Estado del producto
            Paragraph estado = new Paragraph("Descripcion " + estadoProducto.getDescripcion())
                    .setFont(smallFont)
                    .setFontSize(28)
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(estado);

            doc.close();

            log.info("PDF generado para pre-tarima: {}", preTarima.getCodigo());
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    /**
     * Genera código de barras CODE128
     */
    private Image generarCodigoBarras(String codigo) {
        try {
            PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
            Barcode128 barcode = new Barcode128(tempDoc);
            barcode.setCodeType(Barcode128.CODE128);
            barcode.setCode(codigo);
            barcode.setFont(null);

            Image image = new Image(barcode.createFormXObject(tempDoc));
            tempDoc.close();

            return image;
        } catch (Exception e) {
            log.warn("Error creando código de barras: {}", e.getMessage());
            return null;
        }
    }
}