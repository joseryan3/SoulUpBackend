package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.DTO.LoginRequestDTO;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO login,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        String email = login.getEmail() == null ? null : login.getEmail().trim().toLowerCase(Locale.ROOT);
        String senha = login.getSenha();

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email e senha sao obrigatorios."));
        }

        return userRepository.findByEmailIgnoreCase(email)
                .filter(Usuario::isAtivo)
                .filter(usuario -> usuario.getSenha() != null && passwordEncoder.matches(senha, usuario.getSenha()))
                .<ResponseEntity<?>>map(usuario -> {
                    Usuario.TipoUsuario tipo = usuario.getTipo() == null
                            ? Usuario.TipoUsuario.USUARIO
                            : usuario.getTipo();
                    UsernamePasswordAuthenticationToken authentication =
                            UsernamePasswordAuthenticationToken.authenticated(
                                    usuario.getEmail(),
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()))
                            );
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    securityContextRepository.saveContext(context, request, response);

                    Map<String, Object> responseBody = new LinkedHashMap<>();
                    responseBody.put("id", usuario.getId());
                    responseBody.put("nome", usuario.getNome());
                    responseBody.put("email", usuario.getEmail());
                    responseBody.put("premium", usuario.isPremium());
                    responseBody.put("tipo", tipo.name());
                    return ResponseEntity.ok(responseBody);
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Usuario ou senha invalidos.")));
    }

    @PatchMapping("/perfil/visibilidade")
    public ResponseEntity<?> alternarVisibilidade(@RequestParam boolean publico, Authentication authentication) {
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .map(usuario -> {
                    usuario.setPerfilPublico(publico);
                    userRepository.save(usuario);
                    return ResponseEntity.ok("Visibilidade atualizada para: " + (publico ? "Publico" : "Privado"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilizador nao encontrado"));
    }
}