package br.com.insumo.lanchonete.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.insumo.lanchonete.models.MovimentacaoInsumo;

@Repository
public interface MovimentacaoInsumoRepository extends JpaRepository<MovimentacaoInsumo, Long> {

    @Query("SELECT m FROM MovimentacaoInsumo m WHERE m.insumo.id = :insumoId")
    List<MovimentacaoInsumo> findByInsumoId(@Param("insumoId") Long insumoId);

}
