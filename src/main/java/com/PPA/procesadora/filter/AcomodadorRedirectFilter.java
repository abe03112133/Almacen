package com.PPA.procesadora.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AcomodadorRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // NO filtrar APIs - dejarlas pasar
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        // NO filtrar archivos estáticos
        if (requestURI.startsWith("/static/") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") ||
                requestURI.startsWith("/fonts/")) {
            chain.doFilter(request, response);
            return;
        }

        // NO filtrar logout
        if (requestURI.startsWith("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            chain.doFilter(request, response);
            return;
        }

        String rol = auth.getAuthorities().stream()
                .filter(ga -> ga.getAuthority().startsWith("ROLE_"))
                .map(ga -> ga.getAuthority().substring(5))
                .findFirst()
                .orElse(null);

        if (rol == null) {
            chain.doFilter(request, response);
            return;
        }

        // ACOMODADOR y DESCANSERO: Solo pueden acceder a /almacen
        if ("ACOMODADOR".equals(rol) || "DESCANSERO".equals(rol)) {
            if (!requestURI.startsWith("/almacen")) {
                httpResponse.sendRedirect("/almacen/acomodo");
                return;
            }
        }

        // SURTIDOR y CALIDAD: Solo pueden acceder a /surtidos
        if ("SURTIDOR".equals(rol) || "CALIDAD".equals(rol)) {
            if (!requestURI.startsWith("/surtidos")) {
                httpResponse.sendRedirect("/surtidos");
                return;
            }
        }

        // SUPERVISOR, GERENTE, ADMIN: Sin restricciones de redirect
        // (Spring Security ya controla el acceso)

        chain.doFilter(request, response);
    }
}