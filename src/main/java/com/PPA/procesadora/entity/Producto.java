package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @Column(name = "abreviacion", length = 10)
    private String abreviacion;

    @Column(name = "piezas_por_caja", nullable = false)
    private Integer piezasPorCaja = 0;

    @Column(name = "color_display", length = 15)
    private String colorDisplay;

    @Column(name = "activo", columnDefinition = "TINYINT(1)")
    private Boolean activo = true;

}