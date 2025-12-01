package com.PPA.procesadora.util;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Constants {

    // Formato de fecha y hora estándar: ddMMYY HH:mm (24 hrs)
    public static final String FORMATO_FECHA_HORA = "ddMMYY HH:mm";
    public static final DateTimeFormatter FORMATTER_FECHA_HORA = DateTimeFormatter.ofPattern("ddMMYY HH:mm");

    // Zona horaria por defecto (México)
    public static final ZoneId ZONA_HORARIA = ZoneId.of("America/Mexico_City");

    // Estados de Espacio
    public static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    public static final String ESTADO_OCUPADO = "OCUPADO";
    public static final String ESTADO_BLOQUEADO = "BLOQUEADO";

    // Estados de Tarima
    public static final String ESTADO_TARIMA_ACTIVA = "ACTIVA";
    public static final String ESTADO_TARIMA_SURTIDA = "SURTIDA";
    public static final String ESTADO_TARIMA_DEVUELTA = "DEVUELTA";

    // Prefijos de códigos QR
    public static final String PREFIJO_CODIGO_QR = "+C1PACON";
    public static final String PREFIJO_PISO = "PISO-P";
    public static final String PREFIJO_MAQUINA = "M-";

    // Durabilidad de PreTarima (en minutos)
    public static final Integer DURACION_PRE_TARIMA_MINUTOS = 120; // 2 horas

}