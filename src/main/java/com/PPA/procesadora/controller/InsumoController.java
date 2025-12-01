package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Insumo;
import com.PPA.procesadora.entity.Maquina;
import com.PPA.procesadora.entity.ConsumoInsumo;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.service.InsumoService;
import com.PPA.procesadora.service.MaquinaService;
import com.PPA.procesadora.service.ConsumoInsumoService;
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
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
public class InsumoController {

    private final InsumoService insumoService;
    private final MaquinaService maquinaService;
    private final ConsumoInsumoService consumoInsumoService;

    // ====== INSUMOS ======

    /**
     * Obtiene todos los insumos
     * GET /api/insumos
     */
    @GetMapping
    public ResponseEntity<List<Insumo>> obtenerTodos() {
        return ResponseEntity.ok(insumoService.obtenerTodos());
    }

    /**
     * Obtiene un insumo por ID
     * GET /api/insumos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerInsumo(@PathVariable Integer id) {
        try {
            Insumo insumo = insumoService.obtenerPorId(id);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("insumo", insumo);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Crea un insumo
     * POST /api/insumos/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearInsumo(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String descripcion = request.get("descripcion");

            Insumo insumo = insumoService.crear(codigo, descripcion);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Insumo creado exitosamente");
            respuesta.put("insumo", insumo);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Actualiza un insumo
     * POST /api/insumos/{id}/actualizar
     */
    @PostMapping("/{id}/actualizar")
    public ResponseEntity<?> actualizarInsumo(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String descripcion = request.get("descripcion");

            Insumo insumo = insumoService.actualizar(id, codigo, descripcion);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Insumo actualizado exitosamente");
            respuesta.put("insumo", insumo);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Elimina un insumo
     * POST /api/insumos/{id}/eliminar
     */
    @PostMapping("/{id}/eliminar")
    public ResponseEntity<?> eliminarInsumo(@PathVariable Integer id) {
        try {
            insumoService.eliminar(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Insumo eliminado exitosamente");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    // ====== MÁQUINAS ======

    /**
     * Obtiene todas las máquinas
     * GET /api/insumos/maquinas
     */
    @GetMapping("/maquinas")
    public ResponseEntity<List<Maquina>> obtenerMaquinas() {
        return ResponseEntity.ok(maquinaService.obtenerTodas());
    }

    /**
     * Obtiene información de una máquina por código
     * GET /api/insumos/maquinas/info/{codigo}
     * Ej: GET /api/insumos/maquinas/info/M-C300
     */
    @GetMapping("/maquinas/info/{codigo}")
    public ResponseEntity<?> obtenerInfoMaquina(@PathVariable String codigo) {
        try {
            Maquina maquina = maquinaService.obtenerPorCodigo(codigo);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("maquina", new MaquinaDTO(maquina));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Obtiene una máquina por ID
     * GET /api/insumos/maquinas/{id}
     */
    @GetMapping("/maquinas/{id}")
    public ResponseEntity<?> obtenerMaquina(@PathVariable Integer id) {
        try {
            Maquina maquina = maquinaService.obtenerPorId(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("maquina", new MaquinaDTO(maquina));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Crea una máquina
     * POST /api/insumos/maquinas/crear
     */
    @PostMapping("/maquinas/crear")
    public ResponseEntity<?> crearMaquina(@RequestBody Map<String, Object> request) {
        try {
            String codigo = (String) request.get("codigo");
            String nombre = (String) request.get("nombre");
            Integer insumoId = ((Number) request.get("insumoId")).intValue();
            Boolean activo = (Boolean) request.getOrDefault("activo", true);

            Maquina maquina = maquinaService.crear(codigo, nombre, insumoId, activo);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Máquina creada exitosamente");
            respuesta.put("maquina", new MaquinaDTO(maquina));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Actualiza una máquina
     * POST /api/insumos/maquinas/{id}/actualizar
     */
    @PostMapping("/maquinas/{id}/actualizar")
    public ResponseEntity<?> actualizarMaquina(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            String nombre = (String) request.get("nombre");
            Integer insumoId = ((Number) request.get("insumoId")).intValue();
            Boolean activo = (Boolean) request.getOrDefault("activo", true);

            Maquina maquina = maquinaService.actualizar(id, nombre, insumoId, activo);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Máquina actualizada exitosamente");
            respuesta.put("maquina", new MaquinaDTO(maquina));

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    /**
     * Elimina una máquina
     * POST /api/insumos/maquinas/{id}/eliminar
     */
    @PostMapping("/maquinas/{id}/eliminar")
    public ResponseEntity<?> eliminarMaquina(@PathVariable Integer id) {
        try {
            maquinaService.eliminar(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Máquina eliminada exitosamente");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    // ====== CONSUMO DE INSUMO ======

    /**
     * Registra consumo de insumo por máquina escaneada
     * POST /api/insumos/registrar-consumo
     */
    @PostMapping("/registrar-consumo")
    public ResponseEntity<?> registrarConsumo(@RequestBody Map<String, Object> request, Authentication auth) {
        try {
            Integer maquinaId = ((Number) request.get("maquinaId")).intValue();
            Integer insumoId = ((Number) request.get("insumoId")).intValue();
            Object produccionAlmIdObj = request.get("produccionAlmId");
            Integer produccionAlmId = produccionAlmIdObj != null ? ((Number) produccionAlmIdObj).intValue() : null;

            Usuario usuario = new Usuario();
            usuario.setId(1);
            usuario.setUsername(auth.getName());

            ConsumoInsumo consumo = consumoInsumoService.registrar(maquinaId, insumoId, produccionAlmId, usuario);

            // NO devuelvas el objeto completo, solo los datos necesarios
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exitoso", true);
            respuesta.put("mensaje", "Consumo registrado exitosamente");
            respuesta.put("consumoId", consumo.getId());
            respuesta.put("fechaHora", consumo.getFechaHora());
            respuesta.put("maquina", consumo.getMaquina().getNombre());
            respuesta.put("insumo", consumo.getInsumo().getCodigo());

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en consola
            Map<String, Object> error = new HashMap<>();
            error.put("exitoso", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Crea respuesta de error estándar
     */
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("exitoso", false);
        error.put("error", mensaje);
        error.put("timestamp", DateUtils.formatear(DateUtils.ahora()));
        return error;
    }

    // ====== DTOs ======

    /**
     * DTO para máquina con insumo anidado
     */
    public static class MaquinaDTO {
        public Integer id;
        public String codigo;
        public String nombre;
        public Integer insumoId;
        public InsumoDTO insumo;
        public Boolean activo;

        public MaquinaDTO(Maquina maquina) {
            this.id = maquina.getId();
            this.codigo = maquina.getCodigo();
            this.nombre = maquina.getNombre();
            this.activo = maquina.getActivo();
            if (maquina.getInsumo() != null) {
                this.insumoId = maquina.getInsumo().getId();
                this.insumo = new InsumoDTO(maquina.getInsumo());
            }
        }

        // Getters
        public Integer getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public Integer getInsumoId() { return insumoId; }
        public InsumoDTO getInsumo() { return insumo; }
        public Boolean getActivo() { return activo; }
    }

    /**
     * DTO para insumo
     */
    public static class InsumoDTO {
        public Integer id;
        public String codigo;
        public String descripcion;

        public InsumoDTO(Insumo insumo) {
            this.id = insumo.getId();
            this.codigo = insumo.getCodigo();
            this.descripcion = insumo.getDescripcion();
        }

        // Getters
        public Integer getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }
}