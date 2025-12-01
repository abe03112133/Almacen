package com.PPA.procesadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ENTIDAD: SurtidoDiario (ACTUALIZADA)
 *
 * Contenedor de todos los surtidos de un día.
 * Un SurtidoDiario puede tener múltiples SurtidoProducto(s).
 *
 * Estados: ACTIVO -> TERMINADO
 *
 * Flujo:
 * 1. Supervisor crea SurtidoDiario (estado ACTIVO)
 * 2. Agrega SurtidoProducto(s)
 * 3. Sistema genera DetalleSurtido (PEPS)
 * 4. Descarga PDF picklist
 * 5. Surtidor ejecuta con paper
 * 6. Registra diferencias (complementarios, dañadas, no bajadas)
 * 7. Cierra SurtidoDiario (estado TERMINADO)
 */
@Entity
@Table(name = "surtido_diario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurtidoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime fecha;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVO"; // ACTIVO, TERMINADO

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cierre", columnDefinition = "DATETIME(6)")
    private LocalDateTime fechaCierre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_supervisor_id")
    private Usuario supervisorAsignado;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Relación 1:N con SurtidoProducto
    @OneToMany(mappedBy = "surtidoDiario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SurtidoProducto> surtidosProducto;

    public void cerrar() {
        this.estado = "TERMINADO";
        this.fechaCierre = LocalDateTime.now();
    }

    public void reabrir() {
        this.estado = "ACTIVO";
        this.fechaCierre = null;
    }

    public boolean estaCerrado() {
        return "TERMINADO".equals(this.estado);
    }
}