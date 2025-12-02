package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== COMPLEMENTARIO REQUEST =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarComplementarioRequestDTO {
    private Integer productoId;
    private Integer destinoId;
    private Integer cantidad;
}
