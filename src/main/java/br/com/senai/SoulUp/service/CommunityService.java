package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.*;
import br.com.senai.SoulUp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommunityService {

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // RGN26: Método de validação centralizado. Barra o usuário se ele estiver inativo (suspenso)
    @Transactional(readOnly = true)
    public void verificarSeUsuarioEstaAtivo(Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            throw new RuntimeException("Sua conta está suspensa por violação das regras de uso da plataforma.");
        }
    }

    // RGN22: Verifica se o usuário possui assinatura premium ativa e válida
    @Transactional(readOnly = true)
    public boolean verificarAcessoPremium(Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 1. Se o booleano premium for falso, bloqueia o acesso
        if (!usuario.isPremium()) {
            return false;
        }

        // 2. Se tiver data de expiração, verifica se o plano já venceu
        if (usuario.getDataExpiracaoAssinatura() != null &&
                usuario.getDataExpiracaoAssinatura().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }

        return true; // Assinatura ativa e válida!
    }

    // RGN18 + RGN23 + RGN26: Publicar vídeo (Validando suspensão e controle de assinatura)
    public Post publicarVideo(Long videoId, String legenda) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado"));

        Long usuarioId = video.getUsuario().getId().longValue();

        // RGN26: Barra usuários suspensos de publicar
        verificarSeUsuarioEstaAtivo(usuarioId);

        // RGN23: Aplica o bloqueio ativo caso o usuário não seja premium válido
        if (!verificarAcessoPremium(usuarioId)) {
            throw new RuntimeException("Acesso negado. A publicação de conteúdo na comunidade é restrita a assinantes ativos.");
        }

        if (!"CONCLUIDO".equals(video.getStatus())) {
            throw new RuntimeException("Apenas vídeos analisados podem ser publicados na comunidade.");
        }

        Post post = new Post();
        post.setVideo(video);
        post.setUsuario(video.getUsuario());
        post.setLegenda(legenda);

        return postRepository.save(post);
    }

    // RGN18 + RGN20 + RGN23 + RGN26: Adicionar comentário com Moderação, Assinatura e Filtro de Bloqueio
    public void adicionarComentario(Long postId, Long usuarioId, String texto) {
        // RGN26: Barra usuários suspensos de comentar
        verificarSeUsuarioEstaAtivo(usuarioId);

        // RGN23: Bloqueia a ação se o usuário não for pagante ativo
        if (!verificarAcessoPremium(usuarioId)) {
            throw new RuntimeException("Acesso negado. Comentários na comunidade são exclusivos para membros premium.");
        }

        // 1. Filtro de Moderação Automático (RGN20)
        List<String> palavrasBloqueadas = List.of("ofensa1", "palavrao1", "toxicidade");
        String textoMinusculo = texto.toLowerCase();
        for (String palavra : palavrasBloqueadas) {
            if (textoMinusculo.contains(palavra)) {
                throw new RuntimeException("Seu comentário foi bloqueado pelas diretrizes de moderação da comunidade.");
            }
        }

        // 2. Fluxo normal de salvar o comentário
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Usuario autor = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comment comentario = new Comment();
        comentario.setPost(post);
        comentario.setUsuario(autor);
        comentario.setText(texto);

        commentRepository.save(comentario);
    }

    // RGN20: Lógica de Moderação / Denúncia salvando no banco de dados
    public void denunciarPost(Long postId, Long usuarioId, String motivo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Usuario denunciante = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário denunciante não encontrado"));

        Denuncia denuncia = new Denuncia();
        denuncia.setPost(post);
        denuncia.setDenunciante(denunciante);
        denuncia.setMotivo(motivo);

        denunciaRepository.save(denuncia);
    }

    // RGN19 + RGN26: Lógica de Curtidas (Like / Unlike alternável validando suspensão)
    public void alternarCurtida(Long postId, Long usuarioId) {
        // RGN26: Barra usuários suspensos de curtir
        verificarSeUsuarioEstaAtivo(usuarioId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));

        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (post.getCurtidas().contains(usuario)) {
            post.getCurtidas().remove(usuario);
        } else {
            post.getCurtidas().add(usuario);
        }

        postRepository.save(post);
    }

    // RGN21: Retorna o feed de posts ordenado por data decrescente (cronológica)
    @Transactional(readOnly = true)
    public List<Post> listarTodosOsPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // RGN23: Método prático para atualizar e simular ativação de planos de assinatura
    public Usuario atualizarAssinaturaUsuario(Long usuarioId, String planoTipo) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setPremium(true);
        usuario.setPlanoTipo(planoTipo);
        usuario.setDataExpiracaoAssinatura(java.time.LocalDateTime.now().plusDays(30));

        return userRepository.save(usuario);
    }

    // RGN24: Definir e atualizar a imagem de perfil do usuário
    public Usuario atualizarImagemPerfil(Long usuarioId, String novaUrlImagem) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (novaUrlImagem == null || novaUrlImagem.trim().isEmpty()) {
            throw new RuntimeException("O caminho ou URL da imagem não pode ser inválido.");
        }

        usuario.setImagemPerfil(novaUrlImagem);
        return userRepository.save(usuario);
    }

    // RGN26: Método para Moderadores ou Admins suspenderem/ativarem contas por uso indevido
    public void alterarStatusUsuario(Long usuarioAlvoId, Long moderadorId, boolean novoStatus) {
        Usuario moderador = userRepository.findById(moderadorId)
                .orElseThrow(() -> new RuntimeException("Moderador/Admin não encontrado"));

        if (moderador.getTipo() == Usuario.TipoUsuario.USUARIO) {
            throw new RuntimeException("Acesso negado. Apenas moderadores ou administradores podem aplicar punições.");
        }

        Usuario usuarioAlvo = userRepository.findById(usuarioAlvoId)
                .orElseThrow(() -> new RuntimeException("Usuário alvo não encontrado"));

        usuarioAlvo.setAtivo(novoStatus);
        userRepository.save(usuarioAlvo);
    }
}