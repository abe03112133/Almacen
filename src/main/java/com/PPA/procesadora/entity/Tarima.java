package com.PPA.procesadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarima")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo__pre_tarima_id", nullable = false, unique = true)
    @JsonIgnore
    private PreTarima preTarima;

    @Column(name = "codigo", length = 80)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "espacio_id")
    @JsonIgnore
    private Espacio espacio;

    @Column(name = "fecha_almacen", nullable = false)
    private LocalDateTime fechaAlmacen;

    @Column(name = "fecha_surtido")
    private LocalDateTime fechaSurtido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id_almacen")
    private Usuario usuarioAlmacen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id_surtido")
    private Usuario usuarioSurtido;

}