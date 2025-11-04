package br.com.insumo.lanchonete.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.insumo.lanchonete.dtos.AuthenticationInterface;
import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import br.com.insumo.lanchonete.models.Usuario;
import br.com.insumo.lanchonete.models.UsuarioRole;
import br.com.insumo.lanchonete.repositories.UsuarioRepository;
import br.com.insumo.lanchonete.repositories.UsuarioRoleRepository;
import br.com.insumo.lanchonete.utils.SecurityUtils;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioRoleRepository usuarioRoleRepository, 
                         SecurityUtils securityUtils, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.securityUtils = securityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotExistsException("Usuario not found"));
    }

    public Usuario create(Usuario u) {
        // Validação: verificar se email já existe
        if (usuarioRepository.findByUsername(u.getEmail()).size() > 0) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        // Validação: email não pode ser vazio
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        // Validação: senha mínima
        if (u.getSenha() == null || u.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }
        
        // Criptografar senha
        u.setSenha(passwordEncoder.encode(u.getSenha()));
        
        return usuarioRepository.save(u);
    }

    public Usuario update(Usuario u) {
        if (u.getId() == null) throw new EntityNotExistsException("Usuario id required");
        
        // Validação de segurança: usuário só pode atualizar seus próprios dados, exceto se for admin
        if (!securityUtils.isOwnerOrAdmin(u.getId())) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este usuário");
        }
        
        Usuario existing = findById(u.getId());
        
        // Atualizar apenas campos permitidos
        if (u.getNome() != null) {
            existing.setNome(u.getNome());
        }
        
        // Validação: não permitir trocar email se já existe
        if (u.getEmail() != null && !u.getEmail().equals(existing.getEmail())) {
            if (usuarioRepository.findByUsername(u.getEmail()).size() > 0) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            existing.setEmail(u.getEmail());
        }
        
        // Se senha foi fornecida, criptografar
        if (u.getSenha() != null && !u.getSenha().trim().isEmpty()) {
            if (u.getSenha().length() < 6) {
                throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
            }
            existing.setSenha(passwordEncoder.encode(u.getSenha()));
        }
        
        if (u.getTelefone() != null) {
            existing.setTelefone(u.getTelefone());
        }
        
        return usuarioRepository.save(existing);
    }

    public void delete(Long id) {
        // Validação de segurança: usuário só pode deletar sua própria conta, exceto se for admin
        if (!securityUtils.isOwnerOrAdmin(id)) {
            throw new AccessDeniedException("Você não tem permissão para deletar este usuário");
        }
        
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
