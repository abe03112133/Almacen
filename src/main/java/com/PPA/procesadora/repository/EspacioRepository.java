package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Espacio;
import com.PPA.procesadora.entity.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Integer> {

    /**
     * Busca espacios disponibles en un rack, nivel y lado específico
     */
    List<Espacio> findByRackAndNivelAndLadoAndEstadoOrderByPosicion(
            Rack rack, Integer nivel, Integer lado, String estado);

    /**
     * Busca todos los espacios de un rack
     */
    List<Espacio> findByRackOrderByNivelAscLadoAscPosicionAsc(Rack rack);

    /**
     * Busca un espacio específico por rack, nivel, lado y posición
     */
    Optional<Espacio> findByRackAndNivelAndLadoAndPosicion(
            Rack rack, Integer nivel, Integer lado, Integer posicion);

    /**
     * Busca espacios disponibles en un nivel específico de un rack
     */
    List<Espacio> findByRackAndNivelAndEstadoOrderByLadoAscPosicionAsc(
            Rack rack, Integer nivel, String estado);

    /**
     * Busca por código QR
     */
    Optional<Espacio> findByCodigoQr(String codigoQr);

    /**
     * Cuenta espacios disponibles en un nivel/lado
     */
    Long countByRackAndNivelAndLadoAndEstado(
            Rack rack, Integer nivel, Integer lado, String estado);




    /**
     * ALGORITMO PEPS: Busca espacios ordenados por fecha_ocupacion ASC
     *
     * Solo busca en RACKS (no en PISO):
     * - estado = 'OCUPADO'
     * - tarima.producto_id = ?
     * - rack.tipo_rack != 'PISO'
     *
     * Ordena por fecha_ocupacion ASC (más antiguas primero)
     */
    /**
     * ALGORITMO PEPS: Busca espacios ordenados por fecha_ocupacion ASC
     */
    @Query("""
    SELECT e FROM Espacio e
    JOIN e.tarima t
    JOIN t.preTarima pt
    JOIN pt.produccionAlm pa
    WHERE e.estado = 'OCUPADO'
        AND pa.producto.id = :productoId
        AND e.rack.tipoRack.codigo != 'PISO'
    ORDER BY e.fechaOcupacion ASC
    """)
    List<Espacio> findEspaciosDisponiblesPorProductoOrderByFechaPEPS(@Param("productoId") Integer productoId);
    /**
     * Busca el primer espacio PISO disponible (para tarimas dañadas)
     */
    Espacio findFirstByRack_TipoRack_IdAndEstadoOrderByCodigoQr(Integer tipoRackId, String estado);

    /**
     * Obtiene espacios PISO disponibles por prefijo (P1, P2, etc)
     */
    @Query(value = """
        SELECT e.* FROM espacio e
        JOIN rack r ON e.rack_id = r.id
        JOIN tipo_rack tr ON r.tipo_rack_id = tr.id
        WHERE tr.codigo = 'PISO'
            AND r.prefijo_codigo = :prefijo
            AND e.estado = 'DISPONIBLE'
        ORDER BY e.posicion ASC
        """, nativeQuery = true)
    List<Espacio> findEspaciosPisoDisponiblesPorPrefijo(@Param("prefijo") String prefijo);

    /**
     * Cuenta espacios ocupados por tipo de rack
     */
    @Query(value = """
        SELECT COUNT(*) FROM espacio e
        JOIN rack r ON e.rack_id = r.id
        JOIN tipo_rack tr ON r.tipo_rack_id = tr.id
        WHERE tr.codigo = :tipoRackCodigo
            AND e.estado = 'OCUPADO'
        """, nativeQuery = true)
    Long countEspaciosOcupadosPorTipo(@Param("tipoRackCodigo") String tipoRackCodigo);
}