package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoDiarioRequestDTO {
    private LocalDateTime fecha;
    private String descripcion;
    private Integer supervisorId;
}
