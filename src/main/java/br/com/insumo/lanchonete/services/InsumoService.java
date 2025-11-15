package br.com.insumo.lanchonete.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.insumo.lanchonete.enums.TipoMovimentacao;
import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import br.com.insumo.lanchonete.models.Insumo;
import br.com.insumo.lanchonete.models.MovimentacaoInsumo;
import br.com.insumo.lanchonete.repositories.InsumoRepository;
import br.com.insumo.lanchonete.repositories.MovimentacaoInsumoRepository;

@Service
public class InsumoService {

	private static final Logger logger = LoggerFactory.getLogger(InsumoService.class);

	private final InsumoRepository repo;
	private final MovimentacaoInsumoRepository movimentacaoRepo;

	public InsumoService(InsumoRepository repo, MovimentacaoInsumoRepository movimentacaoRepo) {
		this.repo = repo;
		this.movimentacaoRepo = movimentacaoRepo;
	}

	public List<Insumo> findAll() {
		logger.info("Buscando todos os insumos");
		List<Insumo> insumos = repo.findAll();
		logger.info("Total de {} insumo(s) encontrado(s)", insumos.size());
		return insumos;
	}

	public Insumo findById(Long id) {
		logger.info("Buscando insumo com ID: {}", id);
		Insumo insumo = repo.findById(id).orElseThrow(() -> {
			logger.error("Insumo com ID {} não encontrado", id);
			return new EntityNotExistsException("Insumo not found");
		});
		logger.info("Insumo encontrado: {} (Código: {})", insumo.getNome(), insumo.getCodigo());
		return insumo;
	}

	public Insumo create(Insumo i) {
		logger.info("Iniciando criação de novo insumo: {} (Código: {})", i.getNome(), i.getCodigo());
		
		// Validação: código não pode ser duplicado
		Optional<Insumo> existing = repo.findByCodigo(i.getCodigo());
		if (existing.isPresent()) {
			logger.error("Código de insumo {} já cadastrado", i.getCodigo());
			throw new IllegalArgumentException("Código de insumo já cadastrado");
		}
		
		// Validação: quantidade crítica não pode ser negativa
		if (i.getQuantidadeCritica() < 0) {
			logger.error("Tentativa de criar insumo com quantidade crítica negativa: {}", i.getQuantidadeCritica());
			throw new IllegalArgumentException("Quantidade crítica não pode ser negativa");
		}
		
		Insumo saved = repo.save(i);
		logger.info("Insumo criado com sucesso - ID: {}, Nome: {}, Código: {}", saved.getId(), saved.getNome(), saved.getCodigo());
		return saved;
	}

	public Insumo update(Insumo i) {
		logger.info("Iniciando atualização do insumo ID: {}", i.getId());
		
		if (i.getId() == null) {
			logger.error("Tentativa de atualizar insumo sem ID");
			throw new EntityNotExistsException("Insumo id required");
		}
		
		Insumo existing = findById(i.getId());
		logger.debug("Insumo existente encontrado: {}", existing.getNome());
		
		// Validação: código não pode ser duplicado
		if (i.getCodigo() != null && !i.getCodigo().equals(existing.getCodigo())) {
			logger.debug("Verificando alteração de código de {} para {}", existing.getCodigo(), i.getCodigo());
			Optional<Insumo> duplicated = repo.findByCodigo(i.getCodigo());
			if (duplicated.isPresent()) {
				logger.error("Código {} já está em uso por outro insumo", i.getCodigo());
				throw new IllegalArgumentException("Código de insumo já cadastrado");
			}
			existing.setCodigo(i.getCodigo());
			logger.debug("Código atualizado para: {}", i.getCodigo());
		}
		
		if (i.getNome() != null) {
			logger.debug("Atualizando nome de '{}' para '{}'", existing.getNome(), i.getNome());
			existing.setNome(i.getNome());
		}
		
		if (i.getDescricao() != null) {
			logger.debug("Atualizando descrição");
			existing.setDescricao(i.getDescricao());
		}
		
		if (i.getQuantidadeCritica() != null) {
			if (i.getQuantidadeCritica() < 0) {
				logger.error("Tentativa de atualizar com quantidade crítica negativa: {}", i.getQuantidadeCritica());
				throw new IllegalArgumentException("Quantidade crítica não pode ser negativa");
			}
			logger.debug("Atualizando quantidade crítica de {} para {}", existing.getQuantidadeCritica(), i.getQuantidadeCritica());
			existing.setQuantidadeCritica(i.getQuantidadeCritica());
		}
		
		Insumo updated = repo.save(existing);
		logger.info("Insumo ID: {} atualizado com sucesso", updated.getId());
		return updated;
	}

	public void delete(Long id) {
		logger.info("Iniciando exclusão do insumo ID: {}", id);
		Insumo existing = findById(id);
		repo.delete(existing);
		logger.info("Insumo ID: {} ({}) excluído com sucesso", id, existing.getNome());
	}

	public Integer calcularQuantidadeEstoque(Long insumoId) {
		logger.debug("Calculando quantidade em estoque para insumo ID: {}", insumoId);
		List<MovimentacaoInsumo> movimentacoes = movimentacaoRepo.findByInsumoId(insumoId);
		logger.debug("Total de {} movimentação(ões) encontrada(s) para o insumo ID: {}", movimentacoes.size(), insumoId);
		
		int quantidadeEstoque = 0;
		
		for (MovimentacaoInsumo mov : movimentacoes) {
			if (TipoMovimentacao.ENTRADA.name().equals(mov.getTipoMovimentacao())) {
				quantidadeEstoque += mov.getQuantidade();
				logger.trace("Entrada de {} unidades - Estoque atual: {}", mov.getQuantidade(), quantidadeEstoque);
			} else if (TipoMovimentacao.SAIDA.name().equals(mov.getTipoMovimentacao())) {
				quantidadeEstoque -= mov.getQuantidade();
				logger.trace("Saída de {} unidades - Estoque atual: {}", mov.getQuantidade(), quantidadeEstoque);
			}
		}
		
		logger.debug("Quantidade em estoque calculada para insumo ID {}: {}", insumoId, quantidadeEstoque);
		return quantidadeEstoque;
	}

}
