package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Rol;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.RolRepository;
import com.PPA.procesadora.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    /** ============================================================
     *  LISTAR USUARIOS
     *  GET /ppa/usuarios
     * ============================================================ */
    @GetMapping("")
    public String listarUsuarios(Model model) {
        agregarDatosUsuarioActual(model);

        List<UsuarioService.UsuarioDTO> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);

        return "usuarios/usuarios";
    }

    /** ============================================================
     *  FORMULARIO CREAR
     *  GET /ppa/usuarios/nuevo
     * ============================================================ */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        agregarDatosUsuarioActual(model);

        List<String> roles = rolRepository.findAll()
                .stream()
                .map(Rol::getTipo)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        return "usuarios/crear-usuario";
    }

    /** ============================================================
     *  CREAR USUARIO
     *  POST /ppa/usuarios
     * ============================================================ */
    @PostMapping("")
    public String crearUsuario(
            @RequestParam String username,
            @RequestParam String nombreCompleto,
            @RequestParam String password,
            @RequestParam String rolTipo,
            Model model) {

        try {
            UsuarioService.CrearUsuarioDTO dto = new UsuarioService.CrearUsuarioDTO();
            dto.username = username;
            dto.nombreCompleto = nombreCompleto;
            dto.password = password;
            dto.rolTipo = rolTipo;

            usuarioService.crearUsuario(dto);

            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            agregarDatosUsuarioActual(model);
            model.addAttribute("error", e.getMessage());

            List<String> roles = rolRepository.findAll()
                    .stream()
                    .map(Rol::getTipo)
                    .collect(Collectors.toList());
            model.addAttribute("roles", roles);

            return "usuarios/crear-usuario";
        }
    }

    /** ============================================================
     *  FORMULARIO EDITAR
     *  GET /ppa/usuarios/{id}/editar
     * ============================================================ */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        agregarDatosUsuarioActual(model);

        UsuarioService.UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
        model.addAttribute("usuario", usuario);

        List<String> roles = rolRepository.findAll()
                .stream()
                .map(Rol::getTipo)
                .collect(Collectors.toList());
        model.addAttribute("roles", roles);

        return "usuarios/editar-usuario";
    }

    /** ============================================================
     *  ACTUALIZAR USUARIO
     *  POST /ppa/usuarios/{id}
     * ============================================================ */
    @PostMapping("/{id}")
    public String actualizarUsuario(
            @PathVariable Integer id,
            @RequestParam String nombreCompleto,
            @RequestParam String rolTipo,
            @RequestParam(required = false) String activo,
            Model model) {

        try {
            UsuarioService.ActualizarUsuarioDTO dto = new UsuarioService.ActualizarUsuarioDTO();
            dto.nombreCompleto = nombreCompleto;
            dto.rolTipo = rolTipo;
            dto.activo = "true".equals(activo);

            usuarioService.actualizarUsuario(id, dto);

            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            agregarDatosUsuarioActual(model);
            model.addAttribute("error", e.getMessage());

            UsuarioService.UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
            model.addAttribute("usuario", usuario);

            List<String> roles = rolRepository.findAll()
                    .stream()
                    .map(Rol::getTipo)
                    .collect(Collectors.toList());
            model.addAttribute("roles", roles);

            return "usuarios/editar-usuario";
        }
    }

    /** ============================================================
     *  FORM CAMBIAR PASSWORD
     *  GET /ppa/usuarios/{id}/cambiar-password
     * ============================================================ */
    @GetMapping("/{id}/cambiar-password")
    public String mostrarFormularioCambiarPassword(@PathVariable Integer id, Model model) {
        agregarDatosUsuarioActual(model);

        UsuarioService.UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
        model.addAttribute("usuario", usuario);

        return "usuarios/cambiar-password";
    }

    /** ============================================================
     *  POST CAMBIAR PASSWORD
     *  POST /ppa/usuarios/{id}/cambiar-password
     * ============================================================ */
    @PostMapping("/{id}/cambiar-password")
    public String cambiarPassword(
            @PathVariable Integer id,
            @RequestParam String passwordAntigua,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirm,
            Model model) {

        try {

            if (!passwordNueva.equals(passwordConfirm)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }

            usuarioService.cambiarPassword(id, passwordAntigua, passwordNueva);

            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            agregarDatosUsuarioActual(model);
            model.addAttribute("error", e.getMessage());

            UsuarioService.UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
            model.addAttribute("usuario", usuario);

            return "usuarios/cambiar-password";
        }
    }

    /** ============================================================
     *  DESACTIVAR USUARIO
     *  POST /ppa/usuarios/{id}/desactivar
     * ============================================================ */
    @PostMapping("/{id}/desactivar")
    public String desactivarUsuario(@PathVariable Integer id) {
        usuarioService.desactivarUsuario(id);
        return "redirect:/usuarios";
    }

    /** ============================================================
     *  INFO USUARIO LOGUEADO
     * ============================================================ */
    private void agregarDatosUsuarioActual(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
            usuarioOpt.ifPresent(usuario -> model.addAttribute("rol", usuario.getRol().getTipo()));
        }
    }
}
