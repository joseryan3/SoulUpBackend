package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UserRepository usuarioRepository;

    public UsuarioService(UserRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}