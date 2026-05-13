package br.com.fiap.wtchat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public void sendPasswordResetCode(String to, String code) {
        if (mailSender == null || mailFrom.isBlank()) {
            log.warn("Email não configurado. Código de redefinição para {}: {}", to, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject("WTChat — Redefinição de Senha");
            message.setText(
                "Olá!\n\n" +
                "Seu código de redefinição de senha é: " + code + "\n\n" +
                "Este código expira em 15 minutos.\n\n" +
                "Se você não solicitou a redefinição, ignore este email.\n\n" +
                "— Equipe WTChat"
            );
            mailSender.send(message);
            log.info("Código de redefinição enviado para {}", to);
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", to, e.getMessage());
        }
    }
}
