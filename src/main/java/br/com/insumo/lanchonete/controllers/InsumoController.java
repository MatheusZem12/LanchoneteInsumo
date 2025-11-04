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

import br.com.insumo.lanchonete.dtos.InsumoDto;
import br.com.insumo.lanchonete.models.Insumo;
import br.com.insumo.lanchonete.services.InsumoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lanchonete/api/insumos")
public class InsumoController {

    private final InsumoService service;

    public InsumoController(InsumoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<InsumoDto>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findById(id)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<InsumoDto> create(@Valid @RequestBody InsumoDto dto) {
        Insumo created = service.create(fromDto(dto));
        return ResponseEntity.ok(toDto(created));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<InsumoDto> update(@PathVariable Long id, @Valid @RequestBody InsumoDto dto) {
        dto.setId(id);
        Insumo updated = service.update(fromDto(dto));
        return ResponseEntity.ok(toDto(updated));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private InsumoDto toDto(Insumo i) {
        return new InsumoDto(i.getId(), i.getCodigo(), i.getNome(), i.getDescricao(), i.getQuantidadeCritica());
    }

    private Insumo fromDto(InsumoDto dto) {
        return new Insumo(dto.getId(), dto.getCodigo(), dto.getNome(), dto.getDescricao(), dto.getQuantidadeCritica(), null);
    }
}
