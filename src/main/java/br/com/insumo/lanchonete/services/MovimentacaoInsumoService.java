package br.com.insumo.lanchonete.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return repo.findAll();
	}

	public MovimentacaoInsumo findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new EntityNotExistsException("Movimentacao not found"));
	}

	public MovimentacaoInsumo create(MovimentacaoInsumoDto dto) {
		Usuario user = usuarioService.findById(Long.parseLong(dto.getUsuarioId()));
		Insumo insumo = insumoService.findById(dto.getInsumoId());

		// calculate current total based on existing movimentacoes
		int current = insumo.getMovimentacoes() == null ? 0 : insumo.getMovimentacoes().stream().mapToInt(m -> m.getQuantidade() * (m.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1)).sum();
		int delta = dto.getQuantidade() * (dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.ENTRADA.name()) ? 1 : -1);
		int after = current + delta;

		if (after < 0) {
			throw new IllegalArgumentException("Movimentação inválida: resultado negativa");
		}

		if (after == 0 && dto.getTipoMovimentacao().equalsIgnoreCase(TipoMovimentacao.SAIDA.name())) {
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

		// after successful save, if after >0 and after < quantidadeCritica send alert
		if (after > 0 && after < insumo.getQuantidadeCritica()) {
			Map<String, Object> data = new HashMap<>();
			data.put("nome", insumo.getNome());
			data.put("quantidade", after);
			data.put("quantidadeCritica", insumo.getQuantidadeCritica());
			EmailDto email = emailUtils.buildAlert("Alerta: quantidade crítica de insumo", data);
			emailUtils.send(email);
		}

		return saved;
	}

	public MovimentacaoInsumo update(MovimentacaoInsumoDto dto) {
		if (dto.getId() == null) throw new EntityNotExistsException("Id required");
		MovimentacaoInsumo existing = findById(dto.getId());
		existing.setQuantidade(dto.getQuantidade());
		existing.setTipoMovimentacao(dto.getTipoMovimentacao());
		existing.setData(dto.getData() == null ? LocalDateTime.now() : dto.getData());
		return repo.save(existing);
	}

	public void delete(Long id) {
		MovimentacaoInsumo existing = findById(id);
		repo.delete(existing);
	}

}

