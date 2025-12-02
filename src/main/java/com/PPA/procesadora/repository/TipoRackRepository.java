package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.TipoRack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoRackRepository extends JpaRepository<TipoRack, Integer> {
    Optional<TipoRack> findByCodigo(String codigo);
}