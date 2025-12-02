package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Almacen;
import com.PPA.procesadora.entity.Rack;
import com.PPA.procesadora.entity.TipoRack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RackRepository extends JpaRepository<Rack, Integer> {

    /**
     * Busca un rack por su código QR (prefijo + número)
     */
    @Query("SELECT r FROM Rack r WHERE r.prefijoCoigo = :prefijo AND r.numero = :numero")
    Optional<Rack> findByPrefijoAndNumero(@Param("prefijo") String prefijo, @Param("numero") Integer numero);

    /**
     * Busca todos los racks de un almacén
     */
    List<Rack> findByAlmacenOrderByNumero(Almacen almacen);

    /**
     * Busca racks por tipo
     */
    List<Rack> findByTipoRack(TipoRack tipoRack);

    /**
     * Busca racks activos de un almacén
     */
    List<Rack> findByAlmacenAndActivoOrderByNumero(Almacen almacen, Boolean activo);

    /**
     * Busca racks FIJO de un almacén (para sugerencias)
     */
    @Query("SELECT r FROM Rack r WHERE r.almacen = :almacen AND r.tipoRack.codigo = 'FIJO_ALM1' OR r.tipoRack.codigo = 'FIJO_ALM2' ORDER BY r.numero")
    List<Rack> findRacksFixosByAlmacen(@Param("almacen") Almacen almacen);

}