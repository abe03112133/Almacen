package com.PPA.procesadora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rack", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"almacen_id", "numero", "prefijo_codigo"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "numero_dos_digitos", length = 4)
    private String numeroDosDigitos;

    @Column(name = "prefijo_codigo", nullable = false, length = 20)
    private String prefijoCoigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_rack_id", nullable = false)
    private TipoRack tipoRack;

    @Column(name = "niveles_activos", nullable = false)
    private Integer nivelesActivos = 5;

    @Column(name = "espacios_por_nivel", nullable = false)
    private Integer espaciosPorNivel = 9;

    @Column(name = "lados_por_rack", nullable = false)
    private Integer ladosPorRack = 1;

    @Column(name = "es_tunel", columnDefinition = "TINYINT(1)")
    private Boolean esTunel = false;

    @Column(name = "es_pasillo", columnDefinition = "TINYINT(1)")
    private Boolean esPasillo = false;

    @Column(name = "activo", columnDefinition = "TINYINT(1)")
    private Boolean activo = true;

}