package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_rack")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoRack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "niveles", nullable = false)
    private Integer niveles;

    @Column(name = "espacios_por_nivel", nullable = false)
    private Integer espaciosPorNivel;

    @Column(name = "lados_por_rack", nullable = false)
    private Integer ladosPorRack = 1;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

}