package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "produccion_alm")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduccionAlm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produccion_id", nullable = false)
    private Produccion produccion;

    @Column(name = "codigo", nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(name = "Hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "Hora_fin")
    private LocalDateTime horaFin;

    @Column(name = "cajas_iniciales")
    private Integer cajasIniciales = 0;

    @Column(name = "cajas_finales")
    private Integer cajasFinales = 0;

    @Column(name = "estatus", length = 20)
    private String estatus = "ACTIVA";

}