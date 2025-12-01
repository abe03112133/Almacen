package com.PPA.procesadora.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Obtiene la fecha y hora actual en la zona horaria configurada
     */
    public static LocalDateTime ahora() {
        return ZonedDateTime.now(Constants.ZONA_HORARIA).toLocalDateTime();
    }

    /**
     * Formatea una fecha y hora al formato estándar: ddMMYY HH:mm
     */
    public static String formatear(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            return null;
        }
        return fechaHora.format(Constants.FORMATTER_FECHA_HORA);
    }

    /**
     * Parsea un string en formato ddMMYY HH:mm a LocalDateTime
     */
    public static LocalDateTime parsear(String fechaHoraString) {
        if (fechaHoraString == null || fechaHoraString.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(fechaHoraString, Constants.FORMATTER_FECHA_HORA);
    }

    /**
     * Valida si una PreTarima está vigente (menos de 2 horas de creada)
     */
    public static boolean esPreTarimaVigente(LocalDateTime fechaCreacion) {
        LocalDateTime limite = fechaCreacion.plusMinutes(Constants.DURACION_PRE_TARIMA_MINUTOS);
        return ahora().isBefore(limite);
    }

}