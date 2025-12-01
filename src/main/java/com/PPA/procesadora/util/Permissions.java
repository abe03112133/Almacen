package com.PPA.procesadora.util;

import java.util.*;

/**
 * Gestión centralizada de permisos por rol
 */
public class Permissions {

    // Permisos disponibles
    public static final String ALMACENAR_TARIMA = "ALMACENAR_TARIMA";
    public static final String SURTIR_TARIMA = "SURTIR_TARIMA";
    public static final String CAMBIAR_ESTADO_PRODUCTO = "CAMBIAR_ESTADO_PRODUCTO";
    public static final String MOVER_TARIMA = "MOVER_TARIMA";
    public static final String VER_REPORTES = "VER_REPORTES";
    public static final String GESTIONAR_PRODUCTOS = "GESTIONAR_PRODUCTOS";
    public static final String GESTIONAR_PRODUCCIONES = "GESTIONAR_PRODUCCIONES";
    public static final String GESTIONAR_INSUMOS = "GESTIONAR_INSUMOS";
    public static final String GESTIONAR_USUARIOS = "GESTIONAR_USUARIOS";
    public static final String GESTIONAR_RACKS = "GESTIONAR_RACKS";
    public static final String GESTIONAR_ESPACIOS = "GESTIONAR_ESPACIOS";
    public static final String GESTIONAR_MAQUINAS = "GESTIONAR_MAQUINAS";

    /**
     * Mapa de permisos por rol
     */
    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // ACOMODADOR: Solo almacenar tarimas
        ROLE_PERMISSIONS.put("ACOMODADOR", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA
        )));

        // DESCANSERO: Almacenar tarimas (cubre a acomodador)
        ROLE_PERMISSIONS.put("DESCANSERO", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA
        )));

        // SURTIDOR: Almacenar + Surtir tarimas
        ROLE_PERMISSIONS.put("SURTIDOR", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA,
                SURTIR_TARIMA
        )));

        // CALIDAD: Surtidor + Cambiar estado + Mover tarimas
        ROLE_PERMISSIONS.put("CALIDAD", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA,
                SURTIR_TARIMA,
                CAMBIAR_ESTADO_PRODUCTO,
                MOVER_TARIMA
        )));

        // SUPERVISOR: Calidad + Reportes + Gestionar productos/producciones/insumos
        ROLE_PERMISSIONS.put("SUPERVISOR", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA,
                SURTIR_TARIMA,
                CAMBIAR_ESTADO_PRODUCTO,
                MOVER_TARIMA,
                VER_REPORTES,
                GESTIONAR_PRODUCTOS,
                GESTIONAR_PRODUCCIONES,
                GESTIONAR_INSUMOS
        )));

        // GERENTE: Supervisor + Gestionar usuarios
        ROLE_PERMISSIONS.put("GERENTE", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA,
                SURTIR_TARIMA,
                CAMBIAR_ESTADO_PRODUCTO,
                MOVER_TARIMA,
                VER_REPORTES,
                GESTIONAR_PRODUCTOS,
                GESTIONAR_PRODUCCIONES,
                GESTIONAR_INSUMOS,
                GESTIONAR_USUARIOS
        )));

        // ADMIN: Todo
        ROLE_PERMISSIONS.put("ADMIN", new HashSet<>(Arrays.asList(
                ALMACENAR_TARIMA,
                SURTIR_TARIMA,
                CAMBIAR_ESTADO_PRODUCTO,
                MOVER_TARIMA,
                VER_REPORTES,
                GESTIONAR_PRODUCTOS,
                GESTIONAR_PRODUCCIONES,
                GESTIONAR_INSUMOS,
                GESTIONAR_USUARIOS,
                GESTIONAR_RACKS,
                GESTIONAR_ESPACIOS,
                GESTIONAR_MAQUINAS
        )));
    }

    /**
     * Obtiene los permisos de un rol
     */
    public static Set<String> getPermisosDelRol(String rol) {
        return ROLE_PERMISSIONS.getOrDefault(rol, new HashSet<>());
    }

    /**
     * Valida si un rol tiene un permiso específico
     */
    public static boolean tienePermiso(String rol, String permiso) {
        Set<String> permisos = getPermisosDelRol(rol);
        return permisos.contains(permiso);
    }

    /**
     * Obtiene todos los roles disponibles
     */
    public static Set<String> getTodosLosRoles() {
        return ROLE_PERMISSIONS.keySet();
    }

    /**
     * Obtiene descripción del rol
     */
    public static String getDescripcionRol(String rol) {
        switch(rol) {
            case "ACOMODADOR":
                return "Operador que guarda tarimas";
            case "DESCANSERO":
                return "Operador que cubre ocasiones";
            case "SURTIDOR":
                return "Operador que ejecuta picklist";
            case "CALIDAD":
                return "Cambia estado de tarima";
            case "SUPERVISOR":
                return "Valida y autoriza acciones";
            case "GERENTE":
                return "Acceso a reportes y auditorías";
            case "ADMIN":
                return "Administrador del sistema";
            default:
                return "Rol desconocido";
        }
    }
}