package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Integer> {
    Destino findByCodigo(String codigo);
    Destino findByNombre(String nombre);
    List<Destino> findByActivoTrue();
}