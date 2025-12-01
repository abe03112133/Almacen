package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "almacen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero", nullable = false, unique = true)
    private Integer numero;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "prefijo_codigo", nullable = false, length = 10)
    private String prefijoCoigo;

    @Column(name = "activo", columnDefinition = "TINYINT(1)")
    private Boolean activo = true;

}