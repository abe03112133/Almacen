package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {
    Optional<Almacen> findByNumero(Integer numero);
}