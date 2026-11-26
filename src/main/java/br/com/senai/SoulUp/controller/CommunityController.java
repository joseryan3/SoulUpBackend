package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.Post;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidade")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    // RGN18: Publicar um vídeo na comunidade
    @PostMapping("/posts/publicar-video/{videoId}")
    public ResponseEntity<Post> publicarVideo(@PathVariable Long videoId, @RequestParam String legenda) {
        Post novoPost = communityService.publicarVideo(videoId, legenda);
        return ResponseEntity.ok(novoPost);
    }

    // RGN18 + RGN20 + RGN23 + RGN26: Comentar em um Post
    @PostMapping("/posts/{postId}/comentarios")
    public ResponseEntity<String> adicionarComentario(
            @PathVariable Long postId,
            @RequestParam Long usuarioId,
            @RequestBody String texto) {
        communityService.adicionarComentario(postId, usuarioId, texto);
        return ResponseEntity.ok("Comentário adicionado com sucesso!");
    }

    // RGN19 + RGN26: Alternar Curtida (Like/Unlike)
    @PostMapping("/posts/{postId}/curtir")
    public ResponseEntity<String> alternarCurtida(@PathVariable Long postId, @RequestParam Long usuarioId) {
        communityService.alternarCurtida(postId, usuarioId);
        return ResponseEntity.ok("Operação de curtida realizada com sucesso.");
    }

    // RGN20: Denunciar um Post inadequado
    @PostMapping("/posts/{postId}/denunciar")
    public ResponseEntity<String> denunciarPost(
            @PathVariable Long postId,
            @RequestParam Long usuarioId,
            @RequestParam String motivo) {
        communityService.denunciarPost(postId, usuarioId, motivo);
        return ResponseEntity.ok("Denúncia registrada para análise da moderação.");
    }

    // RGN21: Listar feed completo por ordem cronológica decrescente
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> listarFeed() {
        List<Post> feed = communityService.listarTodosOsPosts();
        return ResponseEntity.ok(feed);
    }

    // RGN22: Verificar se um usuário possui acesso premium válido
    @GetMapping("/usuario/{usuarioId}/verificar-premium")
    public ResponseEntity<Boolean> verificarStatusPremium(@PathVariable Long usuarioId) {
        boolean temAcesso = communityService.verificarAcessoPremium(usuarioId);
        return ResponseEntity.ok(temAcesso);
    }

    // RGN23: Simular a ativação/pagamento de um plano de assinatura
    @PutMapping("/usuario/{usuarioId}/assinar")
    public ResponseEntity<String> ativarAssinatura(
            @PathVariable Long usuarioId,
            @RequestParam String planoTipo) {
        Usuario usuario = communityService.atualizarAssinaturaUsuario(usuarioId, planoTipo);
        return ResponseEntity.ok("Assinatura " + planoTipo + " ativada com sucesso para " + usuario.getNome());
    }

    // RGN24: Definir/Atualizar imagem de perfil do usuário
    @PutMapping("/usuario/{usuarioId}/imagem-perfil")
    public ResponseEntity<String> atualizarImagemPerfil(
            @PathVariable Long usuarioId,
            @RequestParam String novaUrlImagem) {
        communityService.atualizarImagemPerfil(usuarioId, novaUrlImagem);
        return ResponseEntity.ok("Imagem de perfil atualizada com sucesso!");
    }

    // RGN26: Moderadores ou Administradores suspendem ou reativam uma conta
    @PutMapping("/usuario/{usuarioAlvoId}/moderar")
    public ResponseEntity<String> moderarUsuario(
            @PathVariable Long usuarioAlvoId,
            @RequestParam Long moderadorId,
            @RequestParam boolean ativo) {
        communityService.alterarStatusUsuario(usuarioAlvoId, moderadorId, ativo);
        String mensagem = ativo ? "Conta reativada com sucesso." : "Conta suspensa com sucesso por uso indevido.";
        return ResponseEntity.ok(mensagem);
    }
}
