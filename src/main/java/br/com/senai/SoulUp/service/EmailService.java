package br.com.senai.SoulUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // MÉTODO GENÉRICO (O que o NotificationService e SupportController precisam)
    public void sendSimpleMessage(String para, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(texto);
        mailSender.send(message);
    }

    // Mantém o seu método de recuperação original (RF01)
    public void enviarEmailRecuperacao(String para, String token) {
        String texto = "Use o token a seguir para resetar sua senha: " + token +
                "\nOu clique no link: " + frontendUrl + "/resetarsenha?token=" + token;

        // Aqui chamamos o método genérico para evitar repetir código
        sendSimpleMessage(para, "Recuperação de Senha - SoulUp", texto);
    }
}
