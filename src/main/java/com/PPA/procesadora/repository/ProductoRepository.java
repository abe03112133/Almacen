package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Buscar producto por código
     */
    Optional<Producto> findByCodigo(String codigo);

    /**
     * Obtener todos los productos activos
     */
    List<Producto> findByActivoTrue();

    /**
     * Obtener todos los productos inactivos
     */
    List<Producto> findByActivoFalse();

    /**
     * Buscar productos por nombre (búsqueda parcial)
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Buscar productos activos por nombre
     */
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}