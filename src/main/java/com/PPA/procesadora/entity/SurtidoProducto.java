package com.PPA.procesadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ENTIDAD: SurtidoProducto (ACTUALIZADA)
 *
 * Un "solicitud de surtido" para UN PRODUCTO a UN DESTINO.
 *
 * Cambios respecto a versión anterior:
 * - Agregada relación FK a SurtidoDiario
 * - Cambio de destino (String) a destino (FK a Destino)
 * - Agregada relación 1:N con DetalleSurtido
 * - Agregados estados y tracking
 *
 * Ejemplo:
 * - SurtidoProducto 1: 100 tarimas PBG -> Monterrey (ACTIVO)
 * - SurtidoProducto 2: 20 tarimas Telera -> Monterrey (ACTIVO)
 *
 * Flujo:
 * 1. Se crea con estado ACTIVO
 * 2. Puede editarse ANTES de generar picklist
 * 3. Al generar, crea DetalleSurtido(s) PENDIENTE
 * 4. Surtidor ejecuta y marca estados de detalles
 * 5. Se puede crear complementario vinculado al mismo SurtidoDiario
 */
@Entity
@Table(name = "surtido_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "surtido_diario_id", nullable = false)
    @JsonIgnore
    private SurtidoDiario surtidoDiario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino; // CAMBIO: FK en lugar de String

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada; // tarimas

    @Column(name = "cantidad_asignada", nullable = false)
    private Integer cantidadAsignada = 0; // tarimas via PEPS

    @Column(name = "cantidad_surtida", nullable = false)
    private Integer cantidadSurtida = 0; // tarimas ejecutadas

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVO"; // ACTIVO, EDITADO, GENERADO, SURTIDO_PARCIAL, SURTIDO_COMPLETO, CANCELADO

    @Column(name = "es_complementario", nullable = false)
    private Boolean esComplementario = false; // Si es SurtidoComplementario (tarimas extra bajadas)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surtido_producto_original_id")
    private SurtidoProducto surtidoProductoOriginal; // Si es complementario, referencia al original

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_edicion", columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaEdicion;

    @Column(name = "fecha_generacion_picklist", columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaGeneracionPicklist;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Relación 1:N con DetalleSurtido
    @OneToMany(mappedBy = "surtidoProducto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleSurtido> detallesSurtido;

    public void marcarGenerado() {
        this.estado = "GENERADO";
        this.fechaGeneracionPicklist = LocalDateTime.now();
    }

    public void marcarSurtido() {
        if (this.cantidadAsignada.equals(this.cantidadSurtida)) {
            this.estado = "SURTIDO_COMPLETO";
        } else {
            this.estado = "SURTIDO_PARCIAL";
        }
    }

    public Integer cantidadFaltante() {
        return this.cantidadSolicitada - this.cantidadSurtida;
    }

    public Integer cantidadNoAsignada() {
        return this.cantidadSolicitada - this.cantidadAsignada;
    }
}