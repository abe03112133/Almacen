package com.PPA.procesadora.controller;

import com.PPA.procesadora.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.PPA.procesadora.entity.*;
import com.PPA.procesadora.repository.*;
import com.PPA.procesadora.service.PDFSurtidoService;
import com.PPA.procesadora.service.SurtidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CONTROLADOR: SurtidoController
 *
 * Endpoints REST para el flujo completo de surtidos PEPS:
 *
 * POST   /api/surtido/crear                      - Crear SurtidoDiario
 * POST   /api/surtido/{id}/producto              - Agregar SurtidoProducto
 * PUT    /api/surtido/producto/{id}              - Editar SurtidoProducto
 * POST   /api/surtido/producto/{id}/generar      - Generar picklist (PEPS)
 * GET    /api/surtido/{id}/picklist/pdf          - Descargar picklist PDF
 * POST   /api/surtido/detalle/{id}/surtido       - Marcar como surtido
 * POST   /api/surtido/detalle/{id}/dañada        - Registrar dañada
 * POST   /api/surtido/detalle/{id}/no-bajada     - Registrar no bajada
 * POST   /api/surtido/{id}/complementario        - Registrar complementario
 * POST   /api/surtido/{id}/cerrar                - Cerrar surtido
 * GET    /api/surtido/{id}/resumen               - Obtener resumen
 */
@Slf4j
@RestController
@RequestMapping("/api/ordenes-surtido")
@RequiredArgsConstructor

public class SurtidoController {

    private final SurtidoService surtidoService;
    private final PDFSurtidoService pdfSurtidoService;
    private final SurtidoDiarioRepository surtidoDiarioRepository;
    private final UsuarioRepository usuarioRepository;

    // ===================================================================
// 0. OBTENER LISTADO DE SURTIDOS
// ===================================================================
    @GetMapping("")
    @Transactional(readOnly = true)
    public ResponseEntity<?> obtenerSurtidos() {

        List<SurtidoDiario> activos = surtidoDiarioRepository.findByEstadoOrderByFechaDesc("ACTIVO");
        List<SurtidoDiario> terminados = surtidoDiarioRepository.findByEstadoOrderByFechaDesc("TERMINADO");

        List<SurtidoDiarioDTO> activosDTO = activos.stream()
                .map(this::toDTOSurtido)
                .toList();

        List<SurtidoDiarioDTO> terminadosDTO = terminados.stream()
                .map(this::toDTOSurtido)
                .toList();

        return ResponseEntity.ok(Map.of(
                "surtidosActivos", activosDTO,
                "surtidosTerminados", terminadosDTO
        ));
    }

    // ===================================================================
    // 1. CREAR SURTIDO DIARIO
    // ===================================================================

    @PostMapping("/crear")
    public ResponseEntity<?> crearSurtidoDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam(required = false) String descripcion) {
        try {
            // Obtener usuario logueado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Usuario supervisor = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            SurtidoDiario sd = surtidoService.crearSurtidoDiario(fecha, descripcion, supervisor);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "data", toDTOSurtido(sd)

            ));
        } catch (Exception e) {
            log.error("Error", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 2. AGREGAR SURTIDO PRODUCTO
    // ===================================================================

    @PostMapping("/{id}/producto")
    public ResponseEntity<?> agregarSurtidoProducto(
            @PathVariable Integer id,
            @RequestBody SurtidoProductoRequestDTO request){
        try {
            Integer productoId = request.getProductoId();
            Integer destinoId = request.getDestinoId();
            Integer cantidad   = request.getCantidad();
            String observaciones = request.getObservaciones();


            SurtidoProducto sp = surtidoService.agregarSurtidoProducto(
                    id, productoId, destinoId, cantidad, observaciones);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "SurtidoProducto agregado",
                    "data", toDTOSurtidoProducto(sp)
            ));
        } catch (Exception e) {
            log.error("❌ Error agregando SurtidoProducto", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 3. EDITAR SURTIDO PRODUCTO
    // ===================================================================

    @PutMapping("/producto/{id}")
    public ResponseEntity<?> editarSurtidoProducto(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer cantidad = request.get("cantidad") != null ?
                    ((Number) request.get("cantidad")).intValue() : null;
            Integer destinoId = request.get("destinoId") != null ?
                    ((Number) request.get("destinoId")).intValue() : null;
            String observaciones = (String) request.get("observaciones");

            SurtidoProducto sp = surtidoService.editarSurtidoProducto(id, cantidad, destinoId, observaciones);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "SurtidoProducto editado",
                    "data", toDTOSurtidoProducto(sp)
            ));
        } catch (Exception e) {
            log.error("❌ Error editando SurtidoProducto", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 4. GENERAR PICKLIST (PEPS)
    // ===================================================================

    @PostMapping("/producto/{id}/generar")
    public ResponseEntity<?> generarPicklist(@PathVariable Integer id) {
        try {
            List<DetalleSurtido> detalles = surtidoService.generarPicklist(id);
            List<DetalleSurtidoDTO> detallesDTO = toDTODetalles(detalles);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Picklist generado con PEPS",
                    "detalles", detallesDTO.size(),
                    "data", detallesDTO
            ));
        } catch (Exception e) {
            log.error("❌ Error generando picklist", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 5. DESCARGAR PICKLIST PDF
    // ===================================================================

    /**
     * Descargar picklist PDF de un SurtidoProducto específico
     * GET /api/ordenes-surtido/{id}/picklist/pdf
     */
    @GetMapping("/{id}/picklist/pdf")
    public ResponseEntity<?> descargarPicklistPDF(@PathVariable Integer id) {
        try {
            byte[] pdfBytes = pdfSurtidoService.generarPicklistPDF(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=picklist_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("❌ Error generando PDF", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Descargar resumen PDF de un SurtidoDiario completo
     * GET /api/ordenes-surtido/diario/{id}/resumen/pdf
     */
    @GetMapping("/diario/{id}/resumen/pdf")
    public ResponseEntity<?> descargarResumenPDF(@PathVariable Integer id) {
        try {
            byte[] pdfBytes = pdfSurtidoService.generarResumenSurtidoDiarioPDF(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resumen_surtido_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("❌ Error generando resumen PDF", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 6. REGISTRAR EJECUCIÓN (SURTIDO, DAÑADA, NO BAJADA)
    // ===================================================================

    @PostMapping("/detalle/{id}/surtido")
    public ResponseEntity<?> marcarSurtido(@PathVariable Integer id) {
        try {
            surtidoService.marcarTarimaSurtida(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tarima marcada como SURTIDA"
            ));
        } catch (Exception e) {
            log.error("❌ Error marcando como surtido", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/detalle/{id}/dañada")
    public ResponseEntity<?> registrarDañada(
            @PathVariable Integer id,
            @RequestParam String motivo) {
        try {
            surtidoService.registrarTarimaDañada(id, motivo);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tarima registrada como DAÑADA y movida a PISO"
            ));
        } catch (Exception e) {
            log.error("❌ Error registrando dañada", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/detalle/{id}/no-bajada")
    public ResponseEntity<?> registrarNoBajada(
            @PathVariable Integer id,
            @RequestParam String motivo) {
        try {
            surtidoService.registrarTarimaNoBajada(id, motivo);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tarima registrada como NO BAJADA"
            ));
        } catch (Exception e) {
            log.error("❌ Error registrando no bajada", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 7. REGISTRAR COMPLEMENTARIO
    // ===================================================================

    @PostMapping("/{id}/complementario")
    public ResponseEntity<?> registrarComplementario(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer productoId = ((Number) request.get("productoId")).intValue();
            Integer destinoId = ((Number) request.get("destinoId")).intValue();
            Integer cantidad = ((Number) request.get("cantidad")).intValue();

            SurtidoProducto complementario = surtidoService.registrarComplementario(
                    id, productoId, destinoId, cantidad);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Complementario registrado",
                    "data", toDTOSurtidoProducto(complementario)
            ));
        } catch (Exception e) {
            log.error("❌ Error registrando complementario", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 8. CERRAR SURTIDO DIARIO
    // ===================================================================

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarSurtidoDiario(@PathVariable Integer id) {
        try {
            SurtidoDiario sd = surtidoService.cerrarSurtidoDiario(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "SurtidoDiario cerrado",
                    "data", sd
            ));
        } catch (Exception e) {
            log.error("❌ Error cerrando surtido", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ===================================================================
    // 9. OBTENER RESUMEN
    // ===================================================================

    @GetMapping("/{id}/resumen")
    @Transactional(readOnly = true)
    public ResponseEntity<?> obtenerResumen(@PathVariable Integer id) {
        try {
            SurtidoDiario sd = surtidoDiarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SurtidoDiario no encontrado"));

            Map<String, Object> resumen = surtidoService.obtenerResumenSurtidoDiario(id);

            // Obtener productos asociados al surtido
            List<SurtidoProducto> productos = sd.getSurtidosProducto();
            List<SurtidoProductoDTO> productosDTO = productos.stream()
                    .map(this::toDTOSurtidoProducto)
                    .toList();

            resumen.put("productos", productosDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", resumen
            ));
        } catch (Exception e) {
            log.error("❌ Error obteniendo resumen", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    private SurtidoDiarioDTO toDTOSurtido(SurtidoDiario s) {
        return new SurtidoDiarioDTO(
                s.getId(),
                s.getFecha(),
                s.getEstado(),
                s.getDescripcion(),
                s.getFechaCreacion(),
                s.getFechaCierre(),
                s.getSupervisorAsignado() != null ? s.getSupervisorAsignado().getId() : null,
                s.getSurtidosProducto() != null ? s.getSurtidosProducto().size() : 0
        );
    }

    private SurtidoProductoDTO toDTOSurtidoProducto(SurtidoProducto sp) {
        return new SurtidoProductoDTO(
                sp.getId(),
                sp.getSurtidoDiario().getId(),
                sp.getProducto().getId(),
                sp.getProducto().getNombre(),
                sp.getDestino().getId(),
                sp.getDestino().getNombre(),
                sp.getCantidadSolicitada(),
                sp.getCantidadAsignada(),
                sp.getCantidadSurtida(),
                sp.getEstado(),
                sp.getEsComplementario(),
                sp.getFechaCreacion(),
                sp.getObservaciones()
        );
    }

    private DetalleSurtidoDTO toDTODetalleSurtido(DetalleSurtido d) {
        return new DetalleSurtidoDTO(
                d.getId(),
                d.getSurtidoProducto().getId(),
                d.getTarima().getId(),
                d.getTarima().getCodigo(),
                d.getCantidadTarimas(),
                d.getEstado(),
                d.getFechaAsignacion(),
                d.getFechaEjecucion(),
                d.getNumeroRackOrigen(),
                d.getNivelOrigen(),
                d.getLadoOrigen(),
                d.getPosicionOrigen(),
                d.getCodigoQrOrigen(),
                d.getObservaciones()
        );
    }

    private SurtidoProducto toEntitySurtidoProducto(SurtidoProductoRequestDTO dto,
                                                    Producto producto,
                                                    Destino destino,
                                                    SurtidoDiario surtido) {

        SurtidoProducto sp = new SurtidoProducto();
        sp.setProducto(producto);
        sp.setDestino(destino);
        sp.setCantidadSolicitada(dto.getCantidad());
        sp.setObservaciones(dto.getObservaciones());
        sp.setEstado("PENDIENTE");
        sp.setSurtidoDiario(surtido);
        sp.setEsComplementario(false);
        sp.setFechaCreacion(LocalDateTime.now());
        return sp;
    }

    private List<DetalleSurtidoDTO> toDTODetalles(List<DetalleSurtido> detalles) {
        return detalles.stream()
                .map(this::toDTODetalleSurtido)
                .toList();
    }


}