package br.com.insumo.lanchonete.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.insumo.lanchonete.dtos.UsuarioDto;
import br.com.insumo.lanchonete.models.Usuario;
import br.com.insumo.lanchonete.services.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lanchonete/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioDto>> findAll() {
        logger.info("GET /lanchonete/api/usuarios - Requisição recebida para listar todos os usuários");
        List<UsuarioDto> response = service.findAll().stream().map(UsuarioDto::new).collect(Collectors.toList());
        logger.info("Response: {} usuário(s) retornado(s)", response.size());
        logger.debug("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> findById(@PathVariable Long id) {
        logger.info("GET /lanchonete/api/usuarios/{} - Requisição recebida", id);
        UsuarioDto response = new UsuarioDto(service.findById(id));
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioDto> create(@Valid @RequestBody UsuarioDto dto) {
        logger.info("POST /lanchonete/api/usuarios - Requisição recebida para criar usuário");
        logger.info("Request DTO: {}", sanitizePasswordLog(dto));
        Usuario created = service.create(fromDto(dto));
        UsuarioDto response = new UsuarioDto(created);
        logger.info("Response DTO: {}", sanitizePasswordLog(response));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Long id, @Valid @RequestBody UsuarioDto dto) {
        logger.info("PUT /lanchonete/api/usuarios/{} - Requisição recebida para atualizar usuário", id);
        logger.info("Request DTO: {}", sanitizePasswordLog(dto));
        dto.setId(id);
        Usuario updated = service.update(fromDto(dto));
        UsuarioDto response = new UsuarioDto(updated);
        logger.info("Response DTO: {}", sanitizePasswordLog(response));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /lanchonete/api/usuarios/{} - Requisição recebida para deletar usuário", id);
        service.delete(id);
        logger.info("Usuário deletado com sucesso - sem response body");
        return ResponseEntity.noContent().build();
    }

    private Usuario fromDto(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTelefone(dto.getTelefone());
        return usuario;
    }

    private String sanitizePasswordLog(UsuarioDto dto) {
        return String.format("UsuarioDto{id=%d, nome='%s', email='%s', senha='***', telefone='%s'}", 
            dto.getId(), dto.getNome(), dto.getEmail(), dto.getTelefone());
    }
}
