package br.com.insumo.lanchonete.utils;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import br.com.insumo.lanchonete.dtos.EmailType;

public class TemplateUtils {

	public static String renderTemplate(EmailType type, Map<String, Object> data) {
		if (type == EmailType.ALERT) {
			return renderAlertTemplate(data);
		} else {
			return renderInfoTemplate(data);
		}
	}

	private static String renderAlertTemplate(Map<String, Object> data) {
		String nome = safe(data.get("nome"));
		Integer quantidade = safeInt(data.get("quantidade"));
		Integer quantidadeCritica = safeInt(data.get("quantidadeCritica"));
		String when = now();

		return "<html><body style=\"font-family:Arial,sans-serif;background:#fff5f5;padding:20px;\">"
				+ "<h2 style=\"color:#b00020;\">⚠ ALERTA DE INSUMO</h2>"
				+ "<p>O insumo <strong>" + nome + "</strong> tem agora <strong>" + quantidade + "</strong> unidades.</p>"
				+ "<p>Quantidade crítica configurada: <strong>" + quantidadeCritica + "</strong></p>"
				+ "<p style=\"color:#b00020;\"><strong>Por favor, reabasteça o insumo.</strong></p>"
				+ "<hr/><small>Gerado em: " + when + "</small>"
				+ "</body></html>";
	}

	private static String renderInfoTemplate(Map<String, Object> data) {
		String title = safe(data.get("title"));
		String body = safe(data.get("body"));
		String when = now();

		return "<html><body style=\"font-family:Arial,sans-serif;background:#f0f7ff;padding:20px;\">"
				+ "<h2 style=\"color:#0366d6;\">" + title + "</h2>"
				+ "<p>" + body + "</p>"
				+ "<hr/><small>Gerado em: " + when + "</small>"
				+ "</body></html>";
	}

	private static String safe(Object o) {
		return o == null ? "" : o.toString();
	}

	private static Integer safeInt(Object o) {
		if (o == null) return 0;
		try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
	}

	private static String now() {
		return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}
