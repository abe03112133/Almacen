package com.PPA.procesadora.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Component
public class GlobalControllerAdvice {

    /**
     * Se ejecuta ANTES de cada método del controlador
     * Agrega username y rol a TODOS los modelos
     * Sin necesidad de inyectar servicios
     */
    @ModelAttribute
    public void agregarDatosGlobales(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();

                // No agregar si es anonymousUser
                if (!username.equals("anonymousUser")) {
                    model.addAttribute("username", username);

                    // Obtener el rol directamente de la autenticación
                    String rol = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .findFirst()
                            .orElse("SIN_ROL")
                            .replace("ROLE_", "");

                    model.addAttribute("rol", rol);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en GlobalControllerAdvice: " + e.getMessage());
        }
    }
}