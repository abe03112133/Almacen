package com.PPA.procesadora.controller;

import com.PPA.procesadora.entity.Insumo;
import com.PPA.procesadora.entity.Maquina;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.service.InsumoService;
import com.PPA.procesadora.service.MaquinaService;
import com.PPA.procesadora.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/insumos")
@RequiredArgsConstructor
@Slf4j
public class InsumosViewController {

    private final InsumoService insumoService;
    private final MaquinaService maquinaService;

    private final UsuarioService usuarioService;

    private void agregarDatosUsuarioActual(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
            usuarioOpt.ifPresent(usuario -> model.addAttribute("rol", usuario.getRol().getTipo()));
        }
    }
    /**
     * Muestra la página de gestión de insumos y máquinas
     * GET /insumos
     */
    @GetMapping
    public String mostrarInsumos(Model model) {
        agregarDatosUsuarioActual(model);
        try {

            List<Insumo> insumos = insumoService.obtenerTodos();

            List<Maquina> maquinas = maquinaService.obtenerTodas();

            model.addAttribute("insumos", insumos);
            model.addAttribute("maquinas", maquinas);
            return "insumos/insumos";
        } catch (Exception e) {
            throw e;
        }
    }

}