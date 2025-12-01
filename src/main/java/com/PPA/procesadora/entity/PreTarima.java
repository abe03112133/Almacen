package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pre_tarima")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreTarima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producccion_alm_Id")
    private ProduccionAlm produccionAlm;

    @Column(name = "cantidad_cajas")
    private Integer cantidadCajas = 25;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produccion_id")
    private Produccion produccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_estado_producto_id")
    private EstadoProducto estadoProducto;

    @Column(name = "hora_creacion", nullable = false)
    private LocalDateTime horaCreacion;

    @Column(name = "codigo", nullable = false, unique = true, length = 80)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

}