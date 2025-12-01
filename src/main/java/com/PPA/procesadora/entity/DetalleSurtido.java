package com.PPA.procesadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * ENTIDAD: DetalleSurtido
 *
 * Un registro por TARIMA asignada a un SurtidoProducto.
 * Se genera automáticamente al aplicar lógica PEPS.
 *
 * Flujo:
 * 1. Supervisor crea SurtidoProducto (ej: 100 tarimas PBG a Monterrey)
 * 2. Sistema busca espacios con tarimas del producto (PEPS)
 * 3. Crea un DetalleSurtido por cada tarima encontrada
 * 4. Estado inicial: PENDIENTE
 * 5. Surtidor ejecuta picklist, marca como SURTIDO/DAÑADA/NO_BAJADA
 *
 * Relación con Tarima:
 * - Una tarima puede tener múltiples DetallesSurtido en su historial
 * - Solo uno activo por surtido (hasta que se cierre)
 */
@Entity
@Table(name = "detalle_surtido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleSurtido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surtido_producto_id", nullable = false)
    @JsonIgnore
    private SurtidoProducto surtidoProducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarima_id", nullable = false)
    @JsonIgnore
    private Tarima tarima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    @JsonIgnore
    private Espacio espacioOrigen; // Espacio donde estaba la tarima cuando se asignó

    @Column(name = "cantidad_tarimas", nullable = false)
    private Integer cantidadTarimas = 1; // Normalmente 1, pero puede ser flexible

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, SURTIDO, DAÑADA, NO_BAJADA

    @Column(name = "fecha_asignacion", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_ejecucion", columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaEjecucion; // Cuando se ejecutó (surtidor bajó la tarima)

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "numero_rack_origen")
    private Integer numeroRackOrigen;

    @Column(name = "nivel_origen")
    private Integer nivelOrigen;

    @Column(name = "lado_origen")
    private Integer ladoOrigen;

    @Column(name = "posicion_origen")
    private Integer posicionOrigen;

    @Column(name = "codigo_qr_origen", length = 120)
    private String codigoQrOrigen; // Para reference en picklist PDF
}