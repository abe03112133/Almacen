package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoProductoRequestDTO {
    private Integer productoId;
    private Integer destinoId;
    private Integer cantidad;
    private String observaciones;
}
