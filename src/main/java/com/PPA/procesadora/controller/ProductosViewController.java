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

import java.util.Optional;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductosViewController {

    private final UsuarioService usuarioService;

    @GetMapping("")
    public String productosPage(Model model) {
        agregarDatosUsuarioActual(model);
        return "productos/productos";
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