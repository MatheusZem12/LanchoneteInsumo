package br.com.insumo.lanchonete.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import br.com.insumo.lanchonete.dtos.EmailDto;
import br.com.insumo.lanchonete.dtos.MovimentacaoInsumoDto;
import br.com.insumo.lanchonete.enums.TipoMovimentacao;
import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import br.com.insumo.lanchonete.models.Insumo;
import br.com.insumo.lanchonete.models.MovimentacaoInsumo;
import br.com.insumo.lanchonete.models.Usuario;
import br.com.insumo.lanchonete.repositories.MovimentacaoInsumoRepository;
import br.com.insumo.lanchonete.utils.EmailUtils;

@Service
@Slf4j
public class MovimentacaoInsumoService {

	private final MovimentacaoInsumoRepository repo;
	private final InsumoService insumoService;
	private final UsuarioService usuarioService;
	private final EmailUtils emailUtils;

	public MovimentacaoInsumoService(MovimentacaoInsumoRepository repo, InsumoService insumoService, UsuarioService usuarioService, EmailUtils emailUtils) {
		this.repo = repo;
		this.insumoService = insumoService;
		this.usuarioService = usuarioService;
		this.emailUtils = emailUtils;
	}

	public List<MovimentacaoInsumo> findAll() {
		log.info("Buscando todas as movimentações de insumos");
		List<MovimentacaoInsumo> movimentacoes = repo.findAll();
		log.info("Total de {} movimentação(ões) encontrada(s)", movimentacoes.size());
		return movimentacoes;
	}

	public MovimentacaoInsumo findById(Long id) {
		log.info("Buscando movimentação com ID: {}", id);
		MovimentacaoInsumo movimentacao = repo.findById(id).orElseThrow(() -> {
			log.error("Movimentação com ID {} não encontrada", id);
			return new EntityNotExistsException("Movimentacao not found");
		});
		log.info("Movimentação encontrada - Tipo: {}, Quantidade: {}", movimentacao.getTipoMovimentacao(), movimentacao.getQuantidade());
		return movimentacao;
	}

	public MovimentacaoInsumo create(MovimentacaoInsumoDto dto) {
		   log.info("Iniciando criação de nova movimentação - Tipo: {}, Quantidade: {}, Insumo ID: {}, Usuario ID: {}", 
			   dto.getTipoMovimentacao(), dto.getQuantidade(), dto.getInsumoId(), dto.getUsuarioId());
		
		Usuario user = usuarioService.findById(dto.getUsuarioId());
		log.debug("Usuário responsável: {} ({})", user.getNome(), user.getEmail());
		
		Insumo insumo = insumoService.findById(dto.getInsumoId());
		log.debug("Insumo: {} - Código: {}, Qtd. Crítica: {}", insumo.getNome(), insumo.getCodigo(), insumo.getQuantidadeCritica());

		// calculate current total based on existing movimentacoes
		int current = insumo.getMovimentacoes() == null ? 0 : insumo.getMovimentacoes().stream().mapToInt(m -> m.getQuantidade() * (m.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1)).sum();
		int delta = dto.getQuantidade() * (dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1);
		int after = current + delta;

		log.debug("Estoque atual: {}, Alteração: {}, Estoque após movimentação: {}", current, delta, after);

		if (after < 0) {
			log.error("Movimentação inválida: estoque resultante seria negativo ({}) para o insumo {}", after, insumo.getNome());
			throw new IllegalArgumentException("Movimentação inválida: resultado negativa");
		}

		if (after == 0 && dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.SAIDA.name())) {
			log.error("Tentativa de zerar o insumo {} não permitida", insumo.getNome());
			throw new IllegalArgumentException("Não é permitido zerar o insumo");
		}

		// save movimentacao
		MovimentacaoInsumo m = new MovimentacaoInsumo();
		m.setQuantidade(dto.getQuantidade());
		m.setTipoMovimentacao(dto.getTipoMovimentacao());
		m.setData(dto.getData() == null ? LocalDateTime.now() : dto.getData());
		m.setUsuario(user);
		m.setInsumo(insumo);

		MovimentacaoInsumo saved = repo.save(m);
		log.info("Movimentação criada com sucesso - ID: {}, Novo estoque: {}", saved.getId(), after);

	// after successful save: only for SAIDA (exit) movimentos, if after > 0 and after < quantidadeCritica send alert
	if (dto.getTipoMovimentacao() != null && dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.SAIDA.name())
		&& after > 0 && after < insumo.getQuantidadeCritica()) {
		log.warn("ALERTA: Insumo '{}' atingiu quantidade crítica! Estoque atual: {}, Quantidade crítica: {}", 
			insumo.getNome(), after, insumo.getQuantidadeCritica());
		Map<String, Object> data = new HashMap<>();
		data.put("nome", insumo.getNome());
		data.put("quantidade", after);
		data.put("quantidadeCritica", insumo.getQuantidadeCritica());
		EmailDto email = emailUtils.buildAlert("Alerta: quantidade crítica de insumo", data);
		log.info("Preparando envio de e-mail crítico: insumo={}, quantidade={}, quantidadeCritica={}, destinatarios={}, titulo={}",
			insumo.getNome(), after, insumo.getQuantidadeCritica(),
			emailUtils != null ? emailUtils.parseRecipients(email, System.getProperty("lanchonete.email.send", "")) : "N/A", email.getTitle());
		emailUtils.sendAsync(email);
		log.info("Chamada assíncrona para envio de e-mail realizada.");
	}
	return saved;
}

	public MovimentacaoInsumo update(MovimentacaoInsumoDto dto) {
		log.info("Iniciando atualização da movimentação ID: {}", dto.getId());
		
		if (dto.getId() == null) {
			log.error("Tentativa de atualizar movimentação sem ID");
			throw new EntityNotExistsException("Id required");
		}
		
		MovimentacaoInsumo existing = findById(dto.getId());
		log.debug("Movimentação existente - Tipo: {}, Quantidade: {}", existing.getTipoMovimentacao(), existing.getQuantidade());
		
		Insumo insumo = existing.getInsumo();
		   log.debug("Insumo da movimentação: {} - Código: {}, Qtd. Crítica: {}", 
			   insumo.getNome(), insumo.getCodigo(), insumo.getQuantidadeCritica());
		
		// Calculate current stock WITHOUT this movimentacao
		int currentWithoutThis = insumo.getMovimentacoes() == null ? 0 : 
			insumo.getMovimentacoes().stream()
				.filter(m -> !m.getId().equals(existing.getId())) // Exclude this movimentacao
				.mapToInt(m -> m.getQuantidade() * (m.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1))
				.sum();
		
		// Calculate stock after update
		int deltaNew = dto.getQuantidade() * (dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1);
		int afterUpdate = currentWithoutThis + deltaNew;
		
		   log.debug("Estoque sem esta movimentação: {}, Nova alteração: {}, Estoque após atualização: {}", 
			   currentWithoutThis, deltaNew, afterUpdate);
		
		// Validation: stock cannot be negative
		if (afterUpdate < 0) {
			   log.error("Atualização inválida: estoque resultante seria negativo ({}) para o insumo {}", 
				   afterUpdate, insumo.getNome());
			throw new IllegalArgumentException("Movimentação inválida: resultado negativo");
		}
		
		// Validation: cannot zero the stock with SAIDA
		if (afterUpdate == 0 && dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.SAIDA.name())) {
			log.error("Tentativa de zerar o insumo {} não permitida", insumo.getNome());
			throw new IllegalArgumentException("Não é permitido zerar o insumo");
		}
		
		// Update the movimentacao
		existing.setQuantidade(dto.getQuantidade());
		existing.setTipoMovimentacao(dto.getTipoMovimentacao());
		existing.setData(dto.getData() == null ? LocalDateTime.now() : dto.getData());
		
		MovimentacaoInsumo updated = repo.save(existing);
		   log.info("Movimentação ID: {} atualizada - Novo tipo: {}, Nova quantidade: {}, Novo estoque: {}", 
			   updated.getId(), updated.getTipoMovimentacao(), updated.getQuantidade(), afterUpdate);
		
	// Check if stock is below critical level and send alert (only when movimentacao is SAIDA)
	if (dto.getTipoMovimentacao() != null && dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.SAIDA.name())
		&& afterUpdate > 0 && afterUpdate < insumo.getQuantidadeCritica()) {
			   log.warn("ALERTA: Insumo '{}' atingiu quantidade crítica após atualização! Estoque atual: {}, Quantidade crítica: {}", 
				   insumo.getNome(), afterUpdate, insumo.getQuantidadeCritica());
			
			Map<String, Object> data = new HashMap<>();
			data.put("nome", insumo.getNome());
			data.put("quantidade", afterUpdate);
			data.put("quantidadeCritica", insumo.getQuantidadeCritica());
			EmailDto email = emailUtils.buildAlert("Alerta: quantidade crítica de insumo", data);
		  emailUtils.sendAsync(email);
		  log.info("Chamada assíncrona para envio de e-mail de alerta após deleção realizada.");
		}
		
		return updated;
	}

	public void delete(Long id) {
		log.info("Iniciando exclusão da movimentação ID: {}", id);
		MovimentacaoInsumo existing = findById(id);
		   log.debug("Movimentação a ser excluída - Insumo: {}, Tipo: {}, Quantidade: {}", 
			   existing.getInsumo().getNome(), existing.getTipoMovimentacao(), existing.getQuantidade());
		
		Insumo insumo = existing.getInsumo();
		
		// Se for uma movimentação do tipo ENTRADA, ao deletar estamos REMOVENDO essa entrada,
		// então o estoque resultante será menor. Precisamos validar se isso deixaria o estoque negativo.
		if (existing.getTipoMovimentacao() != null && existing.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name())) {
			// Calcula o estoque atual
			int currentStock = insumo.getMovimentacoes() == null ? 0 : 
				insumo.getMovimentacoes().stream()
					.mapToInt(m -> m.getQuantidade() * (m.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1))
					.sum();
			
			// Calcula o estoque APÓS remover esta entrada (estoque atual - quantidade da entrada que será deletada)
			int stockAfterDelete = currentStock - existing.getQuantidade();
			
			   log.debug("Estoque atual: {}, Após deletar entrada de {}: {}", currentStock, existing.getQuantidade(), stockAfterDelete);
			
			// Validação: não permitir que o estoque fique negativo
			if (stockAfterDelete < 0) {
				   log.error("Exclusão bloqueada: remover esta entrada deixaria o estoque negativo ({}) para o insumo {}", 
					   stockAfterDelete, insumo.getNome());
				throw new IllegalArgumentException("Não é possível excluir esta movimentação: o estoque ficaria negativo");
			}
			
			// Primeiro deletamos a movimentação
			repo.delete(existing);
			   log.info("Movimentação ID: {} excluída com sucesso", id);
			
			// Depois verificamos se o estoque após a exclusão atingiu a quantidade crítica
			if (stockAfterDelete > 0 && stockAfterDelete < insumo.getQuantidadeCritica()) {
				   log.warn("ALERTA: Ao excluir a movimentação, o insumo '{}' atingiu quantidade crítica! Estoque atual: {}, Quantidade crítica: {}", 
					   insumo.getNome(), stockAfterDelete, insumo.getQuantidadeCritica());
				
				Map<String, Object> data = new HashMap<>();
				data.put("nome", insumo.getNome());
				data.put("quantidade", stockAfterDelete);
				data.put("quantidadeCritica", insumo.getQuantidadeCritica());
				EmailDto email = emailUtils.buildAlert("Alerta: quantidade crítica de insumo", data);
				boolean emailSent = emailUtils.send(email);
				
				if (emailSent) {
					   log.info("E-mail de alerta enviado com sucesso para quantidade crítica do insumo '{}'", insumo.getNome());
				} else {
					   log.warn("Não foi possível enviar e-mail de alerta para o insumo '{}'", insumo.getNome());
				}
			}
		} else {
			// Se for SAIDA, deletar não afeta negativamente o estoque (na verdade aumenta o estoque)
			// Então podemos deletar sem validações extras
			repo.delete(existing);
			   log.info("Movimentação ID: {} excluída com sucesso", id);
		}
	}

}

