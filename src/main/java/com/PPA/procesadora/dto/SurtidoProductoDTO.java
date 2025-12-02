package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ===== SURTIDO PRODUCTO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoProductoDTO {
    private Integer id;
    private Integer surtidoDiarioId;
    private Integer productoId;
    private String productoNombre;
    private Integer destinoId;
    private String destinoNombre;
    private Integer cantidadSolicitada;
    private Integer cantidadAsignada;
    private Integer cantidadSurtida;
    private String estado;
    private Boolean esComplementario;
    private LocalDateTime fechaCreacion;
    private String observaciones;
}
