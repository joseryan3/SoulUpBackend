package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        String foto = oAuth2User.getAttribute("picture");

        Usuario usuarioAtivo = processarUsuarioOAuth2(email, nome, foto);

        String roleName = "ROLE_" + usuarioAtivo.getTipo().name().toUpperCase();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new DefaultOAuth2User(
                Collections.singleton(authority),
                oAuth2User.getAttributes(),
                "email"
        );
    }

    public Usuario processarUsuarioOAuth2(String email, String nome, String foto) {
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user_info"),
                    "E-mail não retornado pelo provedor OAuth2."
            );
        }

        Optional<Usuario> usuarioOptional = userRepository.findByEmail(email);
        Usuario usuarioAtivo;

        if (usuarioOptional.isEmpty()) {
            String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9._-]", "");
            if (baseUsername.isBlank()) {
                baseUsername = "user";
            }

            String usernameFinal = baseUsername;
            if (userRepository.findByUsuario(usernameFinal).isPresent()) {
                usernameFinal = baseUsername + "_" + UUID.randomUUID().toString().substring(0, 6);
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome != null && !nome.isBlank() ? nome : baseUsername);
            novoUsuario.setUsuario(usernameFinal);
            novoUsuario.setEmail(email);
            novoUsuario.setImagemPerfil(foto);
            novoUsuario.setSenha("");
            novoUsuario.setAtivo(true);
            novoUsuario.setPlanoTipo("FREE");
            novoUsuario.setPerfilPublico(true);
            novoUsuario.setTipo(Usuario.TipoUsuario.USUARIO);

            usuarioAtivo = userRepository.save(novoUsuario);
            logger.info("Novo usuário OAuth2 salvo no banco: id={}, email={}", usuarioAtivo.getId(), usuarioAtivo.getEmail());
        } else {
            usuarioAtivo = usuarioOptional.get();
            if (foto != null && !foto.isBlank() && !foto.equals(usuarioAtivo.getImagemPerfil())) {
                usuarioAtivo.setImagemPerfil(foto);
                usuarioAtivo = userRepository.save(usuarioAtivo);
            }
            logger.info("Usuário OAuth2 existente autenticado: id={}, email={}", usuarioAtivo.getId(), usuarioAtivo.getEmail());
        }

        return usuarioAtivo;
    }
}