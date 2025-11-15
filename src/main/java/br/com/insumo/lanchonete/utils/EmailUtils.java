package br.com.insumo.lanchonete.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;

import br.com.insumo.lanchonete.dtos.EmailDto;
import br.com.insumo.lanchonete.dtos.EmailType;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {
	/**
	 * Envia o e-mail de forma assíncrona, usando o pool do Spring.
	 */
	@Async
	public void sendAsync(EmailDto dto) {
		send(dto);
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	private final JavaMailSender mailSender;

	@Value("${lanchonete.email.send}")
	private String configuredRecipients;

	public EmailUtils(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public boolean send(EmailDto dto) {
		List<String> to = parseRecipients(dto, configuredRecipients);
		
		if (to.isEmpty()) {
			logger.warn("Nenhum destinatário configurado para envio de e-mail. Configure 'lanchonete.email.send' no application.properties");
			return false;
		}
		
		try {
			logger.info("=== INICIANDO ENVIO DE E-MAIL ===");
			logger.info("Destinatários: {}", to);
			logger.info("Assunto: '{}'", dto.getTitle());
			logger.info("Tipo: {}", dto.getType());
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			// Configura remetente (usar o mesmo email configurado)
			helper.setFrom(configuredRecipients.split(",")[0].trim());
			helper.setTo(to.toArray(new String[0]));
			helper.setSubject(dto.getTitle());
			
			// Renderiza o template HTML
			String html = TemplateUtils.renderTemplate(dto.getType(), dto.getTemplateData());
			logger.debug("Template HTML renderizado: {} caracteres", html.length());
			
			helper.setText(html, true);
			
			logger.info("Enviando mensagem via SMTP...");
			mailSender.send(message);
			
			logger.info("✅ E-mail enviado com SUCESSO para {}", to);
			logger.info("=== FIM DO ENVIO ===");
			return true;
		} catch (Exception e) {
			logger.error("❌ FALHA ao enviar e-mail: {}", dto.getTitle());
			logger.error("Tipo de erro: {}", e.getClass().getName());
			logger.error("Mensagem de erro: {}", e.getMessage());
			
			// Log da stack trace completa para debug
			logger.error("Stack trace completo:", e);
			
			// Mensagens específicas para problemas comuns
			if (e.getMessage() != null) {
				if (e.getMessage().contains("Authentication")) {
					logger.error("🔒 ERRO DE AUTENTICAÇÃO: Verifique o username e password do Gmail");
					logger.error("💡 DICA: Para Gmail, você precisa usar uma 'Senha de App' ao invés da senha normal");
					logger.error("💡 Gere uma senha de app em: https://myaccount.google.com/apppasswords");
				} else if (e.getMessage().contains("Connection")) {
					logger.error("🌐 ERRO DE CONEXÃO: Não foi possível conectar ao servidor SMTP");
					logger.error("💡 Verifique se o host (smtp.gmail.com) e porta (587) estão corretos");
				} else if (e.getMessage().contains("timeout")) {
					logger.error("⏱️ TIMEOUT: Servidor SMTP não respondeu a tempo");
				}
			}
			
			logger.warn("A aplicação continuará funcionando normalmente sem envio de e-mail.");
			return false;
		}
	}

	public List<String> parseRecipients(EmailDto dto, String configured) {
		if (configured != null && !configured.isBlank()) {
			return Arrays.stream(configured.split(",")).map(String::trim).toList();
		}
		// fallback empty list
		return List.of();
	}

	public EmailDto buildAlert(String title, Map<String, Object> templateData) {
		EmailDto dto = new EmailDto();
		dto.setTitle(title);
		dto.setType(EmailType.ALERT);
		dto.setTemplateData(templateData);
		dto.setSentAt(LocalDateTime.now());
		return dto;
	}

}
