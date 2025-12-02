package com.PPA.procesadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarimaDTO {

    private Integer id;
    private String codigo;
    private String nombreProducto;
    private String abreviacionProducto;
    private Integer cantidadCajas;
    private String fechaAlmacen;
    private String estado;
    private String ubicacion;
    private String estadoProducto;

    // Constructor simple para uso frecuente
    public TarimaDTO(Integer id, String codigo, String nombreProducto, Integer cantidadCajas,
                     String fechaAlmacen, String estado, String ubicacion) {
        this.id = id;
        this.codigo = codigo;
        this.nombreProducto = nombreProducto;
        this.cantidadCajas = cantidadCajas;
        this.fechaAlmacen = fechaAlmacen;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }
}