package br.com.insumo.lanchonete.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.insumo.lanchonete.models.MovimentacaoInsumo;

@Repository
public interface MovimentacaoInsumoRepository extends JpaRepository<MovimentacaoInsumo, Long> {

}
