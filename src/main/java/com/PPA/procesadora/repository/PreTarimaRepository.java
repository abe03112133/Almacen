package com.PPA.procesadora.repository;
import com.PPA.procesadora.entity.PreTarima;
import com.PPA.procesadora.entity.ProduccionAlm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PreTarimaRepository extends JpaRepository<PreTarima, Integer> {
    Optional<PreTarima> findByCodigo(String codigo);

    // Obtener la última pre-tarima de una ProduccionAlm específica
    Optional<PreTarima> findTopByProduccionAlmOrderByIdDesc(ProduccionAlm produccionAlm);

    boolean existsByProduccionAlmId(Integer produccionAlmId);
}