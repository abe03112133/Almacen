package com.PPA.procesadora.config;

import com.PPA.procesadora.util.Permissions;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor para validar permisos en cada petición
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Obtener el rol del usuario desde el atributo de la request
        String rol = (String) request.getAttribute("userRole");
        String permiso = (String) request.getAttribute("requiredPermission");

        // Si no hay permisos requeridos, permitir
        if (permiso == null) {
            return true;
        }

        // Si no hay rol, denegar
        if (rol == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Validar permiso
        if (Permissions.tienePermiso(rol, permiso)) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }
}