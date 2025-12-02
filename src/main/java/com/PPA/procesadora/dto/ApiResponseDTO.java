package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ===== RESPUESTAS COMUNES =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;
    private String error;
}
