package br.com.insumo.lanchonete.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import br.com.insumo.lanchonete.models.Insumo;
import br.com.insumo.lanchonete.repositories.InsumoRepository;

@Service
public class InsumoService {

	private final InsumoRepository repo;

	public InsumoService(InsumoRepository repo) {
		this.repo = repo;
	}

	public List<Insumo> findAll() {
		return repo.findAll();
	}

	public Insumo findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new EntityNotExistsException("Insumo not found"));
	}

	public Insumo create(Insumo i) {
		// Validação: código não pode ser duplicado
		Optional<Insumo> existing = repo.findByCodigo(i.getCodigo());
		if (existing.isPresent()) {
			throw new IllegalArgumentException("Código de insumo já cadastrado");
		}
		
		// Validação: quantidade crítica não pode ser negativa
		if (i.getQuantidadeCritica() < 0) {
			throw new IllegalArgumentException("Quantidade crítica não pode ser negativa");
		}
		
		return repo.save(i);
	}

	public Insumo update(Insumo i) {
		if (i.getId() == null) throw new EntityNotExistsException("Insumo id required");
		
		Insumo existing = findById(i.getId());
		
		// Validação: código não pode ser duplicado
		if (i.getCodigo() != null && !i.getCodigo().equals(existing.getCodigo())) {
			Optional<Insumo> duplicated = repo.findByCodigo(i.getCodigo());
			if (duplicated.isPresent()) {
				throw new IllegalArgumentException("Código de insumo já cadastrado");
			}
			existing.setCodigo(i.getCodigo());
		}
		
		if (i.getNome() != null) {
			existing.setNome(i.getNome());
		}
		
		if (i.getDescricao() != null) {
			existing.setDescricao(i.getDescricao());
		}
		
		if (i.getQuantidadeCritica() != null) {
			if (i.getQuantidadeCritica() < 0) {
				throw new IllegalArgumentException("Quantidade crítica não pode ser negativa");
			}
			existing.setQuantidadeCritica(i.getQuantidadeCritica());
		}
		
		return repo.save(existing);
	}

	public void delete(Long id) {
		Insumo existing = findById(id);
		repo.delete(existing);
	}

}
