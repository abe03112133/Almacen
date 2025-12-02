package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// ===== SURTIDO DIARIO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoDiarioDTO {
    private Integer id;
    private LocalDateTime fecha;
    private String estado;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private Integer supervisorId;
    private Integer totalProductos;
}


