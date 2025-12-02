package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * CONTROLADOR THYMELEAF: SurtidoViewController
 *
 * Renderiza la página de Surtido
 * Las APIs están en SurtidoController.java
 */
@Slf4j
@Controller
@RequestMapping("/surtidos")
@RequiredArgsConstructor
public class SurtidoViewController {

    private final UsuarioService usuarioService;

    /**
     * Retorna la página principal de Surtido
     */
    @GetMapping("")
    public String surtido(Model model) {
        agregarDatosUsuarioActual(model);
        return "surtido/surtido";
    }

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