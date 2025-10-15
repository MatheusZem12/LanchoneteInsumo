package br.com.insumo.lanchonete.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import br.com.insumo.lanchonete.dtos.EmailDto;
import br.com.insumo.lanchonete.dtos.EmailType;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {

	private final JavaMailSender mailSender;

	@Value("${lanchonete.email.send}")
	private String configuredRecipients;

	public EmailUtils(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void send(EmailDto dto) {
		List<String> to = parseRecipients(dto, configuredRecipients);
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(to.toArray(new String[0]));
			helper.setSubject(dto.getTitle());
			String html = TemplateUtils.renderTemplate(dto.getType(), dto.getTemplateData());
			helper.setText(html, true);
			mailSender.send(message);
		} catch (Exception e) {
			// Log and swallow to avoid breaking main flow; real app should log properly
			System.err.println("Falha ao enviar email: " + e.getMessage());
		}
	}

	private List<String> parseRecipients(EmailDto dto, String configured) {
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
