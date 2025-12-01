package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD: Destino
 *
 * Lookup de ciudades/destinos a los que se envían los surtidos.
 * Ejemplos: Monterrey, Guadalajara, Ciudad Juárez, etc.
 *
 * Usado en: SurtidoProducto.destino (FK)
 */
@Entity
@Table(name = "destino")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo; // ej: "MTY", "GDL", "CJS"

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre; // ej: "Monterrey", "Guadalajara"

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}