package com.PPA.procesadora.repository;

import com.PPA.procesadora.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRolId(Integer rolId);

    List<Usuario> findByActivo(Boolean activo);

    List<Usuario> findByActivoTrue();

    boolean existsByUsername(String username);
}