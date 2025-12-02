package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenSurtidoDiarioDTO {
    private Integer id;
    private LocalDateTime fecha;
    private String estado;
    private Integer totalProductos;
    private Integer totalSolicitado;
    private Integer totalAsignado;
    private Integer totalSurtido;
    private Integer porcentajeSurtido;
    private List<DetalleSurtidoDTO> detalles;
}
