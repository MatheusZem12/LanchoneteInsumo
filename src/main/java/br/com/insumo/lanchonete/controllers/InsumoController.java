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

import br.com.insumo.lanchonete.dtos.InsumoDto;
import br.com.insumo.lanchonete.models.Insumo;
import br.com.insumo.lanchonete.services.InsumoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lanchonete/api/insumos")
public class InsumoController {

    private static final Logger logger = LoggerFactory.getLogger(InsumoController.class);

    private final InsumoService service;

    public InsumoController(InsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<InsumoDto>> findAll() {
        logger.info("GET /lanchonete/api/insumos - Requisição recebida para listar todos os insumos");
        List<InsumoDto> response = service.findAll().stream().map(this::toDto).collect(Collectors.toList());
        logger.info("Response: {} insumo(s) retornado(s)", response.size());
        logger.debug("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoDto> findById(@PathVariable Long id) {
        logger.info("GET /lanchonete/api/insumos/{} - Requisição recebida", id);
        InsumoDto response = toDto(service.findById(id));
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<InsumoDto> create(@Valid @RequestBody InsumoDto dto) {
        logger.info("POST /lanchonete/api/insumos - Requisição recebida para criar insumo");
        logger.info("Request DTO: {}", dto);
        Insumo created = service.create(fromDto(dto));
        InsumoDto response = toDto(created);
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<InsumoDto> update(@PathVariable Long id, @Valid @RequestBody InsumoDto dto) {
        logger.info("PUT /lanchonete/api/insumos/{} - Requisição recebida para atualizar insumo", id);
        logger.info("Request DTO: {}", dto);
        dto.setId(id);
        Insumo updated = service.update(fromDto(dto));
        InsumoDto response = toDto(updated);
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /lanchonete/api/insumos/{} - Requisição recebida para deletar insumo", id);
        service.delete(id);
        logger.info("Insumo deletado com sucesso - sem response body");
        return ResponseEntity.noContent().build();
    }

    private InsumoDto toDto(Insumo i) {
        Integer quantidadeEstoque = service.calcularQuantidadeEstoque(i.getId());
        return new InsumoDto(i.getId(), i.getCodigo(), i.getNome(), i.getDescricao(), i.getQuantidadeCritica(), quantidadeEstoque);
    }

    private Insumo fromDto(InsumoDto dto) {
        return new Insumo(dto.getId(), dto.getCodigo(), dto.getNome(), dto.getDescricao(), dto.getQuantidadeCritica(), null);
    }
}
