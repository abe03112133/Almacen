package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.Producto;
import com.PPA.procesadora.entity.ProduccionAlm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduccionAlmRepository extends JpaRepository<ProduccionAlm, Integer> {

    boolean existsByEstatus(String estatus);

    /**
     * Busca producción_alm por código
     */
    Optional<ProduccionAlm> findByCodigo(String codigo);
    /**
     * Busca producciones_alm por id_producion
     */
    List<ProduccionAlm> findByProduccionId(Integer produccionId);

    List<ProduccionAlm> findByProduccionIdAndEstatus(Integer produccionId, String estatus);

    Optional<ProduccionAlm> findByEstatus(String estatus);
    /**
     * Busca producciones_alm activas
     */
    @Query("SELECT pa FROM ProduccionAlm pa WHERE pa.estatus = 'ACTIVA'")
    List<ProduccionAlm> findActivas();

    /**
     * Busca producciones_alm finalizadas
     */
    @Query("SELECT pa FROM ProduccionAlm pa WHERE pa.estatus = 'FINALIZADA'")
    List<ProduccionAlm> findFinalizadas();

    /**
     * Busca producción_alm por código de producto y estatus
     */
    @Query("SELECT pa FROM ProduccionAlm pa WHERE pa.producto.codigo = :codigoProducto AND pa.estatus = :estatus")
    java.util.Optional<ProduccionAlm> findByProductoCodigoAndEstatus(
            @Param("codigoProducto") String codigoProducto,
            @Param("estatus") String estatus);

    /**
     * Busca producción_alm activa por código de producto
     */
    @Query("SELECT pa FROM ProduccionAlm pa WHERE pa.producto.codigo = :codigoProducto AND pa.estatus = :estatus")
    java.util.Optional<ProduccionAlm> findProduccionActivaByProductoCodigo(@Param("codigoProducto") String codigoProducto, @Param("estatus") String estatus);
}