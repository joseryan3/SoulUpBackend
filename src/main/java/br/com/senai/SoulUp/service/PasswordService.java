package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.PasswordResetToken;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.PasswordTokenRepository;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private PasswordTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void solicitarRecuperacao(String email) {
        if (email == null || userRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT)).isEmpty()) {
            return;
        }

        Usuario usuario = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            return;
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, usuario);

        tokenRepository.findByUsuarioId(usuario.getId()).ifPresent(tokenRepository::delete);
        tokenRepository.save(resetToken);
        emailService.enviarEmailRecuperacao(email, token);
    }

    public void resetarSenha(String token, String novaSenhaRaw) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenhaRaw));

        userRepository.save(usuario);
        tokenRepository.delete(resetToken);
    }
}
