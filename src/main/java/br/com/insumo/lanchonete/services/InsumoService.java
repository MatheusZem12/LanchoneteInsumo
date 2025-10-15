package br.com.insumo.lanchonete.services;

import java.util.List;

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
		return repo.save(i);
	}

	public Insumo update(Insumo i) {
		if (i.getId() == null) throw new EntityNotExistsException("Insumo id required");
		Insumo existing = findById(i.getId());
		existing.setCodigo(i.getCodigo());
		existing.setNome(i.getNome());
		existing.setDescricao(i.getDescricao());
		existing.setQuantidadeCritica(i.getQuantidadeCritica());
		return repo.save(existing);
	}

	public void delete(Long id) {
		Insumo existing = findById(id);
		repo.delete(existing);
	}

}
