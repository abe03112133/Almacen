package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioService usuarioService;

    /**
     * Muestra la página de login
     * GET /login
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    /**
     * Dashboard (página principal después de login)
     * GET /dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                model.addAttribute("rol", usuario.getRol().getTipo());
                model.addAttribute("nombreCompleto", usuario.getNombreCompleto());
            }
        }

        return "dashboard";
    }

    /**
     * Logout
     * POST /logout
     * Spring Security maneja la autenticación automáticamente
     */
    @PostMapping("/logout")
    public String logout() {
        // Spring Security limpia la sesión automáticamente
        // No necesitas hacer SecurityContextHolder.clearContext()
        return "redirect:/login";
    }
}