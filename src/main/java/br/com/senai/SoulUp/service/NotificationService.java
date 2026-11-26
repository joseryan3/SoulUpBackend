package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Notification;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.senai.SoulUp.service.EmailService;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    public void notifyUser(Usuario usuario, String title, String message, boolean sendEmail) {
        // 1. Salva a notificação no banco
        Notification notification = new Notification();
        notification.setUsuario(usuario);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // 2. Envio de E-mail (RF11)
        if (sendEmail && usuario.getEmail() != null) {
            try {
                emailService.sendSimpleMessage(usuario.getEmail(), title, message);
            } catch (Exception e) {
                // Log de erro caso o servidor de e-mail falhe,
                // mas a notificação no banco já está salva.
                System.err.println("Erro ao enviar e-mail: " + e.getMessage());
            }
        }
    }
}