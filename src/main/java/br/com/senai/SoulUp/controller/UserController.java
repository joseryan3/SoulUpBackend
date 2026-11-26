package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.DTO.UserEvolutionDTO;
import br.com.senai.SoulUp.DTO.UserPublicDTO;
import br.com.senai.SoulUp.DTO.UserRegistrationDTO;

import br.com.senai.SoulUp.model.Usuario;

import br.com.senai.SoulUp.repository.UserRepository;

import br.com.senai.SoulUp.service.StatsService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {
// ========================================
// REPOSITORY
// ========================================

    @Autowired
    private UserRepository userRepository;

// ========================================
// SERVICE
// ========================================

    @Autowired
    private StatsService statsService;

// ========================================
// CRIPTOGRAFIA
// ========================================

    @Autowired
    private PasswordEncoder passwordEncoder;

// ========================================
// CADASTRO
// ========================================

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserRegistrationDTO dto
    ) {

        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        // Verifica email existente
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email já cadastrado!");
        }

        // Cria usuário
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(email);
        usuario.setUsuario(email);
        usuario.setTipo(Usuario.TipoUsuario.USUARIO);

        // Criptografa senha
        usuario.setSenha(
                passwordEncoder.encode(
                        dto.getSenha()
                )
        );

        // Salva no banco
        try {
            userRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(409).body("E-mail jÃ¡ cadastrado.");
        }

        return ResponseEntity.ok(
                "Usuário cadastrado com sucesso!"
        );
    }

// ========================================
// PERFIL
// ========================================

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obterPerfil(
            @PathVariable Long id
    ) {

        return userRepository.findById(id)

                .map(usuario -> {

                    if (usuario.isPerfilPublico()) {

                        return ResponseEntity.ok(usuario);
                    }

                    UserPublicDTO resumo =
                            new UserPublicDTO(
                                    usuario.getNome(),
                                    usuario.getImagemPerfil()
                            );

                    return ResponseEntity.ok(resumo);
                })

                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

// ========================================
// EVOLUÇÃO
// ========================================

    @GetMapping("/{id}/evolucao")
    public ResponseEntity<UserEvolutionDTO> getEvolucao(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                statsService.calculateEvolution(id)
        );
    }


}
