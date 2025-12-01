package com.PPA.procesadora.filter;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {

        Object status = request.getAttribute("javax.servlet.error.status_code");

        // ✅ SI ES API → NO REDIRIGIR, DEVOLVER ERROR HTTP
        if (request.getRequestURI().startsWith("/api")) {
            if (status != null) {
                response.setStatus(Integer.parseInt(status.toString()));
            }
            return null;
        }

        // =============================
        // SOLO PARA VISTAS HTML
        // =============================

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // 403 Forbidden: Usuario no tiene permisos
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            }

            // 404 Not Found: Página no existe
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }

            // Otros errores 5xx
            if (statusCode >= 500) {
                return "error/500";
            }
        }

        // Error genérico
        return "error/error";
    }
}