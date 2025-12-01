package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "surtido", columnDefinition = "TINYINT(1)")
    private Boolean surtido;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

}