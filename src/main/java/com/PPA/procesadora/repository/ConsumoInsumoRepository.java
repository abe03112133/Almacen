package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.ConsumoInsumo;
import com.PPA.procesadora.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsumoInsumoRepository extends JpaRepository<ConsumoInsumo, Integer> {

    /**
     * Busca consumos por producción_alm
     */
    List<ConsumoInsumo> findByProduccionAlmId(Integer produccionAlmId);

    /**
     * Busca consumos en rango de fechas
     */
    @Query("SELECT c FROM ConsumoInsumo c WHERE c.fechaHora BETWEEN :inicio AND :fin ORDER BY c.fechaHora DESC")
    List<ConsumoInsumo> findByFechaRange(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);


    /**
     * Obtiene todos los consumos de una máquina
     */
    List<ConsumoInsumo> findByMaquinaOrderByFechaHoraDesc(Maquina maquina);

    /**
     * Obtiene consumos en un rango de fecha
     */
    @Query("SELECT c FROM ConsumoInsumo c WHERE c.fechaHora BETWEEN :inicio AND :fin ORDER BY c.fechaHora DESC")
    List<ConsumoInsumo> findByFechaHoraBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    /**
     * Obtiene consumos de una máquina en un rango de fecha
     */
    @Query("SELECT c FROM ConsumoInsumo c WHERE c.maquina.id = :maquinaId AND c.fechaHora BETWEEN :inicio AND :fin ORDER BY c.fechaHora DESC")
    List<ConsumoInsumo> findByMaquinaAndFecha(
            @Param("maquinaId") Integer maquinaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    /**
     * Obtiene consumos de una producción específica
     */
    @Query("SELECT c FROM ConsumoInsumo c WHERE c.produccionAlm.id = :produccionAlmId ORDER BY c.fechaHora DESC")
    List<ConsumoInsumo> findByProduccionAlm(@Param("produccionAlmId") Integer produccionAlmId);
}