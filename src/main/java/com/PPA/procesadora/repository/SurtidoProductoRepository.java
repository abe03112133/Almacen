package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.SurtidoProducto;
import com.PPA.procesadora.entity.SurtidoDiario;
import com.PPA.procesadora.entity.Producto;
import com.PPA.procesadora.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SurtidoProductoRepository extends JpaRepository<SurtidoProducto, Integer> {

    /**
     * Obtiene todos los SurtidoProducto de un SurtidoDiario
     */
    List<SurtidoProducto> findBySurtidoDiario(SurtidoDiario surtidoDiario);

    /**
     * Obtiene SurtidoProducto por estado
     */
    List<SurtidoProducto> findByEstado(String estado);

    /**
     * Obtiene SurtidoProducto por producto
     */
    List<SurtidoProducto> findByProducto(Producto producto);

    /**
     * Obtiene SurtidoProducto por destino
     */
    List<SurtidoProducto> findByDestino(Destino destino);

    /**
     * Obtiene SurtidoProducto complementarios para un original
     */
    @Query("SELECT sp FROM SurtidoProducto sp WHERE sp.surtidoProductoOriginal = :original AND sp.esComplementario = true")
    List<SurtidoProducto> findComplementarios(@Param("original") SurtidoProducto original);

    /**
     * Obtiene SurtidoProducto activos de un SurtidoDiario
     */
    @Query("SELECT sp FROM SurtidoProducto sp WHERE sp.surtidoDiario = :sd AND sp.estado IN ('ACTIVO', 'EDITADO', 'GENERADO')")
    List<SurtidoProducto> findActivosBySurtidoDiario(@Param("sd") SurtidoDiario surtidoDiario);

    /**
     * Obtiene SurtidoProducto no asignados (ACTIVO o EDITADO, sin detalle)
     */
    @Query("SELECT sp FROM SurtidoProducto sp WHERE sp.estado IN ('ACTIVO', 'EDITADO') AND sp.cantidadAsignada = 0")
    List<SurtidoProducto> findNoAsignados();
}