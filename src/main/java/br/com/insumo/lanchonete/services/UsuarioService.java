package br.com.insumo.lanchonete.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

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
        logger.info("Buscando todos os usuários");
        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.info("Total de {} usuário(s) encontrado(s)", usuarios.size());
        return usuarios;
    }

    public Usuario findById(Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> {
            logger.error("Usuário com ID {} não encontrado", id);
            return new EntityNotExistsException("Usuario not found");
        });
        logger.info("Usuário encontrado: {} ({})", usuario.getNome(), usuario.getEmail());
        return usuario;
    }

    public Usuario create(Usuario u) {
        logger.info("Iniciando criação de novo usuário: {} ({})", u.getNome(), u.getEmail());
        
        // Validação: verificar se email já existe
        if (usuarioRepository.findByUsername(u.getEmail()).size() > 0) {
            logger.error("Email {} já está cadastrado", u.getEmail());
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        // Validação: email não pode ser vazio
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            logger.error("Tentativa de criar usuário sem email");
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        // Validação: senha mínima
        if (u.getSenha() == null || u.getSenha().length() < 6) {
            logger.error("Senha fornecida não atende o requisito mínimo de 6 caracteres");
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }
        
        // Criptografar senha
        logger.debug("Criptografando senha do usuário");
        u.setSenha(passwordEncoder.encode(u.getSenha()));
        
        Usuario saved = usuarioRepository.save(u);
        logger.info("Usuário criado com sucesso - ID: {}, Nome: {}, Email: {}", saved.getId(), saved.getNome(), saved.getEmail());
        return saved;
    }

    public Usuario update(Usuario u) {
        logger.info("Iniciando atualização do usuário ID: {}", u.getId());
        
        if (u.getId() == null) {
            logger.error("Tentativa de atualizar usuário sem ID");
            throw new EntityNotExistsException("Usuario id required");
        }
        
        // Validação de segurança: usuário só pode atualizar seus próprios dados, exceto se for admin
        if (!securityUtils.isOwnerOrAdmin(u.getId())) {
            logger.warn("Tentativa não autorizada de atualizar usuário ID: {}", u.getId());
            throw new AccessDeniedException("Você não tem permissão para atualizar este usuário");
        }
        
        Usuario existing = findById(u.getId());
        logger.debug("Usuário existente encontrado: {}", existing.getNome());
        
        // Atualizar apenas campos permitidos
        if (u.getNome() != null) {
            logger.debug("Atualizando nome de '{}' para '{}'", existing.getNome(), u.getNome());
            existing.setNome(u.getNome());
        }
        
        // Validação: não permitir trocar email se já existe
        if (u.getEmail() != null && !u.getEmail().equals(existing.getEmail())) {
            logger.debug("Tentativa de alteração de email de {} para {}", existing.getEmail(), u.getEmail());
            if (usuarioRepository.findByUsername(u.getEmail()).size() > 0) {
                logger.error("Email {} já está em uso por outro usuário", u.getEmail());
                throw new IllegalArgumentException("Email já cadastrado");
            }
            existing.setEmail(u.getEmail());
            logger.debug("Email atualizado com sucesso");
        }
        
        // Se senha foi fornecida, criptografar
        if (u.getSenha() != null && !u.getSenha().trim().isEmpty()) {
            if (u.getSenha().length() < 6) {
                logger.error("Nova senha não atende o requisito mínimo de 6 caracteres");
                throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
            }
            logger.debug("Atualizando senha do usuário");
            existing.setSenha(passwordEncoder.encode(u.getSenha()));
        }
        
        if (u.getTelefone() != null) {
            logger.debug("Atualizando telefone para: {}", u.getTelefone());
            existing.setTelefone(u.getTelefone());
        }
        
        Usuario updated = usuarioRepository.save(existing);
        logger.info("Usuário ID: {} atualizado com sucesso", updated.getId());
        return updated;
    }

    public void delete(Long id) {
        logger.info("Iniciando exclusão do usuário ID: {}", id);
        
        // Validação de segurança: usuário só pode deletar sua própria conta, exceto se for admin
        if (!securityUtils.isOwnerOrAdmin(id)) {
            logger.warn("Tentativa não autorizada de deletar usuário ID: {}", id);
            throw new AccessDeniedException("Você não tem permissão para deletar este usuário");
        }
        
        Usuario existing = findById(id);
        usuarioRepository.delete(existing);
        logger.info("Usuário ID: {} ({}) excluído com sucesso", id, existing.getNome());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Carregando detalhes do usuário: {}", username);
        List<AuthenticationInterface> dto = usuarioRepository.findByUsername(username);

        if(dto.isEmpty()) {
            logger.error("Usuário {} não encontrado no banco de dados", username);
            throw new UsernameNotFoundException("User not found");
        }

        logger.debug("Usuário encontrado, carregando roles");
        Usuario user = new Usuario();
        user.setEmail(dto.get(0).getEmail());
        user.setSenha(dto.get(0).getSenha());

        if (user.getUsuarioRoleList() == null) {
            user.setUsuarioRoleList(new ArrayList<>());
        }

        int roleCount = 0;
        for (AuthenticationInterface role : dto) {
            UsuarioRole usuarioRole = usuarioRoleRepository.findById(role.getIdRole())
                .orElseThrow(() -> {
                    logger.error("Role com ID {} não encontrada", role.getIdRole());
                    return new UsernameNotFoundException("Role not found");
                });
            user.getUsuarioRoleList().add(usuarioRole);
            logger.debug("Role adicionada: {}", usuarioRole.getRole().getNome());
            roleCount++;
        }

        logger.info("Usuário {} carregado com sucesso com {} role(s)", username, roleCount);
        return user;
    }


}
