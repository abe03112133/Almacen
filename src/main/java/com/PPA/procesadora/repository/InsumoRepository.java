package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    Optional<Insumo> findByCodigo(String codigo);
}