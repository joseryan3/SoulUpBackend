package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Usuario getRequiredUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Autenticação necessária.");
        }

        String identificador = extrairEmailOuIdentificador(authentication);
        if (identificador == null || identificador.isBlank()) {
            throw new AccessDeniedException("Usuário autenticado não encontrado.");
        }

        return userRepository.findByEmailIgnoreCase(identificador)
                .or(() -> userRepository.findByUsuario(identificador))
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado."));
    }

    private String extrairEmailOuIdentificador(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            if (oidcUser.getEmail() != null && !oidcUser.getEmail().isBlank()) {
                return oidcUser.getEmail();
            }
        } else if (principal instanceof OAuth2User oauth2User) {
            Object emailAttr = oauth2User.getAttribute("email");
            if (emailAttr != null && !emailAttr.toString().isBlank()) {
                return emailAttr.toString();
            }
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return authentication.getName();
    }
}