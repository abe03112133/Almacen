package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Integer> {

    /**
     * Busca una máquina por código
     */
    Optional<Maquina> findByCodigo(String codigo);

    /**
     * Obtiene todas las máquinas activas
     */
    List<Maquina> findByActivoTrue();

    /**
     * Obtiene todas las máquinas de un insumo específico
     */
    List<Maquina> findByInsumoId(Integer insumoId);
}