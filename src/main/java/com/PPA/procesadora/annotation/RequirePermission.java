package com.PPA.procesadora.annotation;

import java.lang.annotation.*;

/**
 * Anotación para marcar qué permiso requiere un endpoint
 * Ejemplo: @RequirePermission(Permissions.ALMACENAR_TARIMA)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String value();
}