package br.com.senai.SoulUp.config;

import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner criarAdminPadrao(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@soulup.com";

            if (userRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador SoulUp");
                admin.setUsuario("admin");
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode("Admin@123"));
                admin.setTipo(Usuario.TipoUsuario.ADMIN);
                admin.setAtivo(true);
                admin.setPerfilPublico(false);

                userRepository.save(admin);
                System.out.println("Conta ADMIN criada: " + adminEmail);
            }
        };
    }
}