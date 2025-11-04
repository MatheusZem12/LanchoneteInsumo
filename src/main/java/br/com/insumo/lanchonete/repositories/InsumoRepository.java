package br.com.insumo.lanchonete.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.insumo.lanchonete.models.Insumo;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    Optional<Insumo> findByCodigo(String codigo);
}
