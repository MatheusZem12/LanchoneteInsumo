package br.com.insumo.lanchonete.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.insumo.lanchonete.dtos.AuthenticationInterface;
import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import br.com.insumo.lanchonete.models.Usuario;
import br.com.insumo.lanchonete.models.UsuarioRole;
import br.com.insumo.lanchonete.repositories.UsuarioRepository;
import br.com.insumo.lanchonete.repositories.UsuarioRoleRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioRoleRepository usuarioRoleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotExistsException("Usuario not found"));
    }

    public Usuario create(Usuario u) {
        return usuarioRepository.save(u);
    }

    public Usuario update(Usuario u) {
        if (u.getId() == null) throw new EntityNotExistsException("Usuario id required");
        Usuario existing = findById(u.getId());
        existing.setNome(u.getNome());
        existing.setEmail(u.getEmail());
        existing.setSenha(u.getSenha());
        existing.setTelefone(u.getTelefone());
        return usuarioRepository.save(existing);
    }

    public void delete(Long id) {
        Usuario existing = findById(id);
        usuarioRepository.delete(existing);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<AuthenticationInterface> dto = usuarioRepository.findByUsername(username);

        if(dto.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Usuario user = new Usuario();
        user.setEmail(dto.get(0).getEmail());
        user.setSenha(dto.get(0).getSenha());

        if (user.getUsuarioRoleList() == null) {
            user.setUsuarioRoleList(new ArrayList<>());
        }

        for (AuthenticationInterface role : dto) {
            UsuarioRole usuarioRole = usuarioRoleRepository.findById(role.getIdRole())
                .orElseThrow(() -> new UsernameNotFoundException("Role not found"));
            user.getUsuarioRoleList().add(usuarioRole);
        }

        return user;
    }


}
