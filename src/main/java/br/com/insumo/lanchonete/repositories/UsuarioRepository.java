package br.com.insumo.lanchonete.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.insumo.lanchonete.dtos.AuthenticationInterface;
import br.com.insumo.lanchonete.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(
        nativeQuery = true,
        value =
        "SELECT u.email as email, u.senha as senha, ur.role_id as idRole "
        + "FROM tb_usuarios u INNER JOIN tb_usuarios_roles ur ON u.id = ur.user_id "
        + "WHERE u.email = ?1"
    )
    List<AuthenticationInterface> findByUsername(String username);

}
