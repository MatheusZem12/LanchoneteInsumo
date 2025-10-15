package br.com.insumo.lanchonete.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.insumo.lanchonete.models.UsuarioRole;

@Repository
public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Long> {

}
