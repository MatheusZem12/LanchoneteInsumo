package br.com.insumo.lanchonete.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {
    @Async
    public void enviarEmailAsync(String destinatario, String assunto, String corpo) {
        log.info("Enviando email para {} de forma assíncrona...", destinatario);
        // Simulação do envio de email
        try {
            Thread.sleep(2000); // Simula tempo de envio
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Email enviado para {} com assunto: {}", destinatario, assunto);
    }
}
