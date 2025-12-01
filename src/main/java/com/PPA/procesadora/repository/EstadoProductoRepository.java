package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoProductoRepository extends JpaRepository<EstadoProducto, Integer> {
    Optional<EstadoProducto> findByCodigo(String codigo);
}