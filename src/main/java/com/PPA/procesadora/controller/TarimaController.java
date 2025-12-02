package com.PPA.procesadora.controller;

import com.PPA.procesadora.dto.TarimaDTO;
import com.PPA.procesadora.entity.EstadoProducto;
import com.PPA.procesadora.entity.ProduccionAlm;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.EstadoProductoRepository;
import com.PPA.procesadora.repository.ProduccionAlmRepository;
import com.PPA.procesadora.service.EspacioService;
import com.PPA.procesadora.service.PreTarimaService;
import com.PPA.procesadora.service.TarimaService;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarimas")
@RequiredArgsConstructor
public class TarimaController {

    private final PreTarimaService preTarimaService;
    private final TarimaService tarimaService;
    private final EspacioService espacioService;
    private final EstadoProductoRepository estadoProductoRepository;
    private final ProduccionAlmRepository produccionAlmRepository;

    /**
     * Obtiene lista de estados de producto
     * GET /api/tarimas/estados-producto
     */
    @GetMapping("/estados-producto")
    public ResponseEntity<List<EstadoProducto>> obtenerEstadosProducto() {
        return ResponseEntity.ok(estadoProductoRepository.findAll());
    }

    /**
     * Obtiene la producción activa actual
     * GET /api/tarimas/produccion-activa
     */
    @GetMapping("/produccion-activa")
    public ResponseEntity<?> obtenerProduccionActiva() {
        try {
            java.util.Optional<ProduccionAlm> produccionAlmOpt = produccionAlmRepository.findByEstatus("ACTIVA");

            if (!produccionAlmOpt.isPresent()) {
                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("exitoso", false);
                respuesta.put("mensaje", "No hay producción activa");
                respuesta.put("produccionAlm", null);
                return ResponseEntity.ok(respuesta);
            }

            ProduccionAlm produccionAlm = produccionAlmOpt.get();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Producción activa encontrada");

            Map<String, Object> produccionData = new HashMap<>();
            produccionData.put("id", produccionAlm.getId());
            produccionData.put("codigo", produccionAlm.getCodigo());
            produccionData.put("nombreProducto", produccionAlm.getProducto().getNombre());
            produccionData.put("abreviacionProducto", produccionAlm.getProducto().getAbreviacion());
            produccionData.put("codigoProducto", produccionAlm.getProducto().getCodigo());
            produccionData.put("estatus", produccionAlm.getEstatus());
            produccionData.put("horaInicio", produccionAlm.getHoraInicio());
            produccionData.put("horaFin", produccionAlm.getHoraFin());
            produccionData.put("numeroTariminasActual", "T001");

            // TODO: Verificar si existen PreTarimas para esta ProduccionAlm
            // boolean tienePretarimas = preTarimaRepository.existsByProduccionAlmId(produccionAlm.getId());
            // produccionData.put("tienePretarimas", tienePretarimas);

            respuesta.put("produccionAlm", produccionData);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exitoso", false, "error", e.getMessage()));
        }
    }

    /**
     * Obtiene información de una pre-tarima sin crear tarima
     * GET /api/tarimas/obtener-pretarima/{codigo}
     */
    @GetMapping("/obtener-pretarima/{codigo}")
    public ResponseEntity<?> obtenerPreTarima(@PathVariable String codigo) {
        try {
            PreTarimaService.PreTarimaDTO preTarima = preTarimaService.getPreTarimaInfo(codigo);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Pre-tarima encontrada");
            respuesta.put("preTarima", preTarima);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error: " + e.getMessage()));
        }
    }

    /**
     * Paso 1: Escanea etiqueta de caja y crea pre-tarima
     * Formato escaneado: *0 + codigoProducto + fecha + *
     * Ej: *0242052251125*
     *
     * Esperado: POST /api/tarimas/crear-pretarima
     * Body: {
     *   "codigoEscaneado": "*0242052251125*",
     *   "estadoProducto": "TERMINADO"   (default: TERMINADO)
     * }
     * Respuesta: PreTarima con código 251125-PBG1-T001
     */
    @PostMapping("/crear-pretarima")
    public ResponseEntity<?> crearPreTarimaDesdeEtiqueta(@RequestBody Map<String, String> request, Authentication auth) {
        try {
            String codigoEscaneado = request.get("codigoEscaneado");
            String estadoProducto = request.getOrDefault("estadoProducto", "TERMINADO");

            if (codigoEscaneado == null || codigoEscaneado.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("Código escaneado requerido (formato: *0CODIGOPRODUCTOFECHA*)"));
            }

            Usuario usuario = new Usuario(); // TODO: Obtener del Authentication
            usuario.setId(1);
            usuario.setUsername("admin");

            PreTarimaService.PreTarimaDTO preTarima = preTarimaService.crearPreTarimaDesdeCodigoEscaneado(
                    codigoEscaneado, estadoProducto, usuario
            );

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Pre-tarima creada. Escanea el código para generar tarima");
            respuesta.put("preTarima", preTarima);
            respuesta.put("proximoPaso", "Escanea: " + preTarima.getCodigo());

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al crear pre-tarima: " + e.getMessage()));
        }
    }

    /**
     * Escanea código de pre-tarima y crea la tarima
     * Esperado: POST /api/tarimas/crear-tarima-desde-pretarima
     * Body: { "codigoPreTarima": "251125-PBG1-T001" }
     */
    @PostMapping("/crear-tarima-desde-pretarima")
    public ResponseEntity<?> crearTarimaDesdePreTarima(@RequestBody Map<String, String> request, Authentication auth) {
        try {
            String codigoPreTarima = request.get("codigoPreTarima");
            if (codigoPreTarima == null || codigoPreTarima.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("Código de pre-tarima requerido"));
            }

            Usuario usuario = new Usuario(); // TODO: Obtener del Authentication
            usuario.setId(1);
            usuario.setUsername("admin");

            PreTarimaService.PreTarimaDTO preTarima = preTarimaService.getPreTarimaInfo(codigoPreTarima);
            TarimaService.TarimaDTO tarima = tarimaService.crearTarima(codigoPreTarima, usuario);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Tarima creada, esperando escaneo de slot");
            respuesta.put("tarima", tarima);
            respuesta.put("estado", "ESPERANDO_SLOT");

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al crear tarima: " + e.getMessage()));
        }
    }

    /**
     * Paso 2: Escanea slot y obtiene espacio PEPS
     * Retorna espacios RESTANTES después de asignar la tarima
     * Esperado: GET /api/tarimas/info-slot/{codigoQr}
     */
    @GetMapping("/info-slot/{codigoQr}")
    public ResponseEntity<?> obtenerInfoSlot(@PathVariable String codigoQr) {
        try {
            EspacioService.EspacioInfoDTO info = espacioService.getEspacioInfo(codigoQr);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", info.getExitoso());
            respuesta.put("almacen", info.getAlmacenNumero());
            respuesta.put("rack", info.getRackNumero());
            respuesta.put("nivel", info.getNivel());
            respuesta.put("lado", info.getLado());
            respuesta.put("tipoRack", info.getTipoRack());

            if (info.getExitoso()) {
                // Espacio PEPS disponible
                respuesta.put("posicionDisponible", info.getPosicionDisponible());
                respuesta.put("espaciosRestantes", info.getEspaciosDisponibles());
                respuesta.put("mensaje", info.getEspaciosDisponibles() > 0
                        ? String.format("✓ Espacio asignado. Quedan %d espacios", info.getEspaciosDisponibles())
                        : "✓ Espacio asignado. Este nivel está COMPLETO");
                respuesta.put("estado", "ASIGNADO");
            } else {
                // Sin espacios disponibles
                respuesta.put("espaciosRestantes", 0);
                respuesta.put("estado", "LLENO");
                respuesta.put("error", "Nivel/lado LLENO. Continúa en: " + info.getSugerencia());
            }

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al procesar slot: " + e.getMessage()));
        }
    }

    /**
     * Paso 3: Confirma almacenamiento de tarima en espacio
     * Esperado: POST /api/tarimas/guardar-en-espacio
     * Body: { "tarimaId": 1, "espacioId": 5 }
     */
    @PostMapping("/guardar-en-espacio")
    public ResponseEntity<?> guardarEnEspacio(@RequestBody Map<String, Integer> request, Authentication auth) {
        try {
            Integer tarimaId = request.get("tarimaId");
            Integer espacioId = request.get("espacioId");

            if (tarimaId == null || espacioId == null) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("tarimaId y espacioId requeridos"));
            }

            Usuario usuario = new Usuario(); // TODO: Obtener del Authentication
            usuario.setId(1);

            TarimaService.TarimaDTO tarima = tarimaService.guardarTarimaEnEspacio(tarimaId, espacioId, usuario);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Tarima almacenada exitosamente");
            respuesta.put("tarima", tarima);
            respuesta.put("estado", "ALMACENADA");

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al guardar tarima: " + e.getMessage()));
        }
    }

    /**
     * Obtiene información de una tarima
     * GET /api/tarimas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTarima(@PathVariable Integer id) {
        try {
            TarimaService.TarimaDTO tarima = tarimaService.obtenerInfoTarima(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("tarima", tarima);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error: " + e.getMessage()));
        }
    }

    /**
     * Marca tarima como surtida
     * POST /api/tarimas/{id}/marcar-surtida
     */
    @PostMapping("/{id}/marcar-surtida")
    public ResponseEntity<?> marcarSurtida(@PathVariable Integer id, Authentication auth) {
        try {
            Usuario usuario = new Usuario(); // TODO: Obtener del Authentication
            usuario.setId(1);

            tarimaService.marcarComosurtida(id, usuario);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Tarima marcada como surtida");
            respuesta.put("estado", "SURTIDA");

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error: " + e.getMessage()));
        }
    }

    /**
     * Crea respuesta estándar de error
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("exitoso", false);
        error.put("error", mensaje);
        error.put("timestamp", DateUtils.formatear(DateUtils.ahora()));
        return error;
    }

}