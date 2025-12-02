package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarSurtidoProductoRequestDTO {
    private Integer cantidad;
    private Integer destinoId;
    private String observaciones;
}
