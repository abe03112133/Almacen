package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ===== DETALLE SURTIDO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleSurtidoDTO {
    private Integer id;
    private Integer surtidoProductoId;
    private Integer tarimaId;
    private String tarimaCodigo;
    private Integer cantidadTarimas;
    private String estado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEjecucion;
    private Integer numeroRackOrigen;
    private Integer nivelOrigen;
    private Integer ladoOrigen;
    private Integer posicionOrigen;
    private String codigoQrOrigen;
    private String observaciones;
}
