package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Tarima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarimaRepository extends JpaRepository<Tarima, Integer> {

    /**
     * Busca tarima por su código
     */
    Optional<Tarima> findByCodigo(String codigo);

    /**
     * Busca tarimas por pre-tarima ID
     */
    Optional<Tarima> findByPreTarimaId(Integer preTarimaId);

    /**
     * Busca tarimas no surtidas (sin fecha_surtido)
     */
    @Query("SELECT t FROM Tarima t WHERE t.fechaSurtido IS NULL ORDER BY t.fechaAlmacen ASC")
    List<Tarima> findTarimasNoSurtidas();

    /**
     * Busca tarimas en un espacio específico
     */
    @Query("SELECT t FROM Tarima t WHERE t.espacio.id = :espacioId")
    Optional<Tarima> findByEspacioId(@Param("espacioId") Integer espacioId);

    /**
     * Busca tarimas por producto en rango de fechas
     */
    @Query("SELECT t FROM Tarima t WHERE t.preTarima.produccionAlm.producto.id = :productoId " +
            "AND t.fechaAlmacen BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY t.fechaAlmacen DESC")
    List<Tarima> findByProductoAndFechaRange(
            @Param("productoId") Integer productoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca tarimas por producción
     */
    @Query("SELECT t FROM Tarima t WHERE t.preTarima.produccionAlm.id = :produccionAlmId")
    List<Tarima> findByProduccionAlm(@Param("produccionAlmId") Integer produccionAlmId);

}