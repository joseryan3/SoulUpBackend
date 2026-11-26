package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Post;
import br.com.senai.SoulUp.model.Report;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.PostRepository;
import br.com.senai.SoulUp.repository.ReportRepository;
import br.com.senai.SoulUp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportService {

    // Adicione o repositório no topo da sua CommunityService
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;



    // Método da RGN19
    public void denunciarPost(Long postId, Long usuarioId, String motivo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Report report = new Report();
        report.setPost(post);
        report.setUsuario(usuario);
        report.setReason(motivo);

        reportRepository.save(report);
    }
}
