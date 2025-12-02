package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.SurtidoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurtidoDiarioRepository extends JpaRepository<SurtidoDiario, Integer> {

    /**
     * Obtiene surtidos activos (no terminados)
     */
    List<SurtidoDiario> findByEstadoOrderByFechaDesc(String estado);

    /**
     * Obtiene surtidos por rango de fechas
     */
    @Query("SELECT sd FROM SurtidoDiario sd WHERE sd.fecha >= :fechaInicio AND sd.fecha <= :fechaFin ORDER BY sd.fecha DESC")
    List<SurtidoDiario> findByRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene surtido más reciente activo
     */
    @Query("SELECT sd FROM SurtidoDiario sd WHERE sd.estado = 'ACTIVO' ORDER BY sd.fechaCreacion DESC LIMIT 1")
    SurtidoDiario findUltimoActivo();

    /**
     * Cuenta surtidos activos
     */
    Long countByEstado(String estado);
}