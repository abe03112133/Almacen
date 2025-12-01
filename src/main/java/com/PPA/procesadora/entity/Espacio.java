package com.PPA.procesadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "espacio", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"rack_id", "nivel", "lado", "posicion"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;

    @Column(name = "nivel", nullable = false)
    private Integer nivel = 0;

    @Column(name = "lado", nullable = false)
    private Integer lado = 1;

    @Column(name = "posicion", nullable = false)
    private Integer posicion;

    @Column(name = "codigo_qr", length = 120)
    private String codigoQr;

    @Column(name = "estado", length = 30)
    private String estado = "DISPONIBLE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarima_id")
    @JsonIgnore
    private Tarima tarima;

    @Column(name = "fecha_ocupacion")
    private LocalDateTime fechaOcupacion;

    @Column(name = "fecha_liberacion")
    private LocalDateTime fechaLiberacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloqueado_por")
    @JsonIgnore
    private Usuario bloqueadoPor;

}