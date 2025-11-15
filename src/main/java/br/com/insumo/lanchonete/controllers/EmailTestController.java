package br.com.insumo.lanchonete.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.insumo.lanchonete.dtos.EmailDto;
import br.com.insumo.lanchonete.utils.EmailUtils;

/**
 * Controller para testar o envio de e-mails.
 * REMOVER EM PRODUÇÃO - Apenas para desenvolvimento/debug.
 */
@RestController
@RequestMapping("/api/test")
public class EmailTestController {

	private static final Logger logger = LoggerFactory.getLogger(EmailTestController.class);
	
	private final EmailUtils emailUtils;

	public EmailTestController(EmailUtils emailUtils) {
		this.emailUtils = emailUtils;
	}

	/**
	 * Endpoint para testar envio de e-mail.
	 * Acesse: http://localhost:8080/api/test/email
	 */
	@GetMapping("/email")
	public ResponseEntity<Map<String, Object>> testEmail() {
		logger.info("=== TESTE DE ENVIO DE E-MAIL INICIADO ===");
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			// Dados de teste para o alerta
			Map<String, Object> templateData = new HashMap<>();
			templateData.put("nome", "Insumo de Teste");
			templateData.put("quantidade", 5);
			templateData.put("quantidadeCritica", 10);
			
			// Constrói o e-mail de alerta
			EmailDto emailDto = emailUtils.buildAlert(
				"[TESTE] Alerta de quantidade crítica", 
				templateData
			);
			
			// Tenta enviar
			boolean success = emailUtils.send(emailDto);
			
			response.put("success", success);
			response.put("message", success 
				? "E-mail de teste enviado com sucesso! Verifique a caixa de entrada."
				: "Falha ao enviar e-mail. Verifique os logs do servidor para detalhes."
			);
			response.put("timestamp", java.time.LocalDateTime.now().toString());
			
			if (success) {
				logger.info("✅ Teste de e-mail concluído com SUCESSO");
				return ResponseEntity.ok(response);
			} else {
				logger.warn("⚠️ Teste de e-mail FALHOU - veja logs acima para detalhes");
				return ResponseEntity.status(500).body(response);
			}
			
		} catch (Exception e) {
			logger.error("❌ ERRO INESPERADO no teste de e-mail", e);
			response.put("success", false);
			response.put("message", "Erro inesperado: " + e.getMessage());
			response.put("error", e.getClass().getName());
			return ResponseEntity.status(500).body(response);
		}
	}
}
