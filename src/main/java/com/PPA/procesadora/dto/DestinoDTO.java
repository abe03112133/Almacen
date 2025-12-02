package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; /**
 * DTOs: Request/Response para APIs
 *
 * Ventajas:
 * - No expone relaciones complejas
 * - Controla qué datos se envían/reciben
 * - Facilita versionado de APIs
 */

// ===== DESTINO =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinoDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private Boolean activo;
}
