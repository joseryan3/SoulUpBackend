package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.DTO.NewPasswordDTO;
import br.com.senai.SoulUp.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/senha")
public class PasswordController {
    @Autowired
    private PasswordService passwordService;

    @PostMapping("/recuperar")
    public ResponseEntity<?> esqueciSenha(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("Email e obrigatorio.");
            }

            passwordService.solicitarRecuperacao(email);
            return ResponseEntity.ok("E-mail de recuperacao enviado!");
        } catch (Exception e) {
            return ResponseEntity.status(503).body("ServiÃ§o de e-mail indisponÃ­vel. Tente novamente mais tarde.");
        }
    }

    @PostMapping("/resetar")
    public ResponseEntity<?> resetarSenha(@Valid @RequestBody NewPasswordDTO dto) {
        try {
            passwordService.resetarSenha(dto.getToken(), dto.getNewPassword());
            return ResponseEntity.ok("Senha alterada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
