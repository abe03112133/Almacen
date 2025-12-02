package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.DetalleSurtido;
import com.PPA.procesadora.entity.SurtidoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleSurtidoRepository extends JpaRepository<DetalleSurtido, Integer> {

    /**
     * Obtiene todos los detalles de un SurtidoProducto
     */
    List<DetalleSurtido> findBySurtidoProducto(SurtidoProducto surtidoProducto);

    /**
     * Obtiene detalles por estado
     */
    List<DetalleSurtido> findByEstado(String estado);

    /**
     * Cuenta tarimas por estado en un SurtidoProducto
     */
    Long countBySurtidoProductoAndEstado(SurtidoProducto surtidoProducto, String estado);

    /**
     * Obtiene detalles pendientes de un SurtidoProducto (para picklist)
     */
    @Query("SELECT ds FROM DetalleSurtido ds WHERE ds.surtidoProducto = :sp AND ds.estado = 'PENDIENTE'")
    List<DetalleSurtido> findPendientesBySurtidoProducto(@Param("sp") SurtidoProducto surtidoProducto);

    /**
     * Obtiene detalles dañadas para un SurtidoProducto
     */
    @Query("SELECT ds FROM DetalleSurtido ds WHERE ds.surtidoProducto = :sp AND ds.estado = 'DAÑADA'")
    List<DetalleSurtido> findDañadasBySurtidoProducto(@Param("sp") SurtidoProducto surtidoProducto);

    /**
     * Obtiene detalles no bajadas para un SurtidoProducto
     */
    @Query("SELECT ds FROM DetalleSurtido ds WHERE ds.surtidoProducto = :sp AND ds.estado = 'NO_BAJADA'")
    List<DetalleSurtido> findNoBajadasBySurtidoProducto(@Param("sp") SurtidoProducto surtidoProducto);

    /**
     * Obtiene detalles surtidas para un SurtidoProducto
     */
    @Query("SELECT ds FROM DetalleSurtido ds WHERE ds.surtidoProducto = :sp AND ds.estado = 'SURTIDO'")
    List<DetalleSurtido> findSurtidasBySurtidoProducto(@Param("sp") SurtidoProducto surtidoProducto);
}