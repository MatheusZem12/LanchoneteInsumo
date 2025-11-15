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

import br.com.insumo.lanchonete.dtos.MovimentacaoInsumoDto;
import br.com.insumo.lanchonete.models.MovimentacaoInsumo;
import br.com.insumo.lanchonete.services.MovimentacaoInsumoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lanchonete/api/movimentacoes_insumos")
public class MovimentacaoInsumoController {

    private static final Logger logger = LoggerFactory.getLogger(MovimentacaoInsumoController.class);

    private final MovimentacaoInsumoService service;

    public MovimentacaoInsumoController(MovimentacaoInsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoInsumoDto>> findAll() {
        logger.info("GET /lanchonete/api/movimentacoes_insumos - Requisição recebida para listar todas as movimentações");
        List<MovimentacaoInsumoDto> response = service.findAll().stream().map(this::toDto).collect(Collectors.toList());
        logger.info("Response: {} movimentação(ões) retornada(s)", response.size());
        logger.debug("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoInsumoDto> findById(@PathVariable Long id) {
        logger.info("GET /lanchonete/api/movimentacoes_insumos/{} - Requisição recebida", id);
        MovimentacaoInsumoDto response = toDto(service.findById(id));
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<MovimentacaoInsumoDto> create(@Valid @RequestBody MovimentacaoInsumoDto dto) {
        logger.info("POST /lanchonete/api/movimentacoes_insumos - Requisição recebida para criar movimentação");
        logger.info("Request DTO: {}", dto);
        MovimentacaoInsumo created = service.create(dto);
        MovimentacaoInsumoDto response = toDto(created);
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MovimentacaoInsumoDto> update(@PathVariable Long id, @Valid @RequestBody MovimentacaoInsumoDto dto) {
        logger.info("PUT /lanchonete/api/movimentacoes_insumos/{} - Requisição recebida para atualizar movimentação", id);
        logger.info("Request DTO: {}", dto);
        dto.setId(id);
        MovimentacaoInsumo updated = service.update(dto);
        MovimentacaoInsumoDto response = toDto(updated);
        logger.info("Response DTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /lanchonete/api/movimentacoes_insumos/{} - Requisição recebida para deletar movimentação", id);
        service.delete(id);
        logger.info("Movimentação deletada com sucesso - sem response body");
        return ResponseEntity.noContent().build();
    }

    private MovimentacaoInsumoDto toDto(MovimentacaoInsumo m) {
        MovimentacaoInsumoDto dto = new MovimentacaoInsumoDto();
        dto.setId(m.getId());
        dto.setQuantidade(m.getQuantidade());
        dto.setTipoMovimentacao(m.getTipoMovimentacao());
        dto.setData(m.getData());
        dto.setUsuarioId(m.getUsuario().getId());
        dto.setInsumoId(m.getInsumo().getId());
        dto.setInsumoNome(m.getInsumo().getNome());
        dto.setInsumoCodigo(m.getInsumo().getCodigo());
        return dto;
    }
}
