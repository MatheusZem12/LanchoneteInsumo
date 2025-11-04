package br.com.insumo.lanchonete.controllers;

import java.util.List;
import java.util.stream.Collectors;

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

    private final MovimentacaoInsumoService service;

    public MovimentacaoInsumoController(MovimentacaoInsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoInsumoDto>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoInsumoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findById(id)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<MovimentacaoInsumoDto> create(@Valid @RequestBody MovimentacaoInsumoDto dto) {
        MovimentacaoInsumo created = service.create(dto);
        return ResponseEntity.ok(toDto(created));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MovimentacaoInsumoDto> update(@PathVariable Long id, @Valid @RequestBody MovimentacaoInsumoDto dto) {
        dto.setId(id);
        MovimentacaoInsumo updated = service.update(dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private MovimentacaoInsumoDto toDto(MovimentacaoInsumo m) {
        return new MovimentacaoInsumoDto(m.getId(), m.getQuantidade(), m.getTipoMovimentacao(), m.getData(), String.valueOf(m.getUsuario().getId()), m.getInsumo().getId());
    }
}
