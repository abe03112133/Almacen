package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Produccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {

    List<Produccion> findByActivoTrue();

    List<Produccion> findByActivoFalse();

    List<Produccion> findAll();

    Optional<Produccion> findTopByActivoTrueOrderByFechaDesc();

}