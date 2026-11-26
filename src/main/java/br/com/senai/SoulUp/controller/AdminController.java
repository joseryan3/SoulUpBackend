package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.SupportTicket;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.model.Video;
import br.com.senai.SoulUp.repository.SupportTicketRepository;
import br.com.senai.SoulUp.repository.UserRepository;
import br.com.senai.SoulUp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    private static final DateTimeFormatter DATA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public record UsuarioItem(Integer id, String nome, String email, String cadastro, String status, String tipo) {}
    public record VideoItem(Long id, String usuario, String exercicio, String data, String status, String precisao) {}
    public record MensagemItem(String autor, String texto) {}
    public record TicketItem(Long id, String assunto, String usuario, String categoria, String prioridade, String status, String data, List<MensagemItem> mensagens) {}

    // ==================== USUÁRIOS ====================

    @GetMapping("/users")
    public List<UsuarioItem> listarUsuarios() {
        List<UsuarioItem> lista = new ArrayList<>();

        for (Usuario u : userRepository.findAll()) {
            lista.add(new UsuarioItem(
                    u.getId(),
                    u.getNome(),
                    u.getEmail(),
                    "—",
                    u.isAtivo() ? "Ativo" : "Inativo",
                    u.getTipo() != null ? u.getTipo().name() : "USUARIO"
            ));
        }
        return lista;
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<?> alternarStatusUsuario(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(usuario -> {
                    usuario.setAtivo(!usuario.isAtivo());
                    userRepository.save(usuario);
                    return ResponseEntity.ok(Map.of(
                            "id", usuario.getId(),
                            "status", usuario.isAtivo() ? "Ativo" : "Inativo"
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== VÍDEOS ====================

    @GetMapping("/videos")
    public List<VideoItem> listarVideos() {
        List<VideoItem> lista = new ArrayList<>();

        for (Video v : videoRepository.findAll()) {
            lista.add(new VideoItem(
                    v.getId(),
                    v.getUsuario() != null ? v.getUsuario().getNome() : "Anônimo",
                    v.getTipoExercicio() != null ? v.getTipoExercicio() : v.getFileName(),
                    v.getCreatedAt() != null ? v.getCreatedAt().format(DATA_FMT) : "—",
                    formatarStatusVideo(v.getStatus()),
                    v.getScore() != null ? Math.round(v.getScore()) + "%" : "—"
            ));
        }
        return lista;
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> excluirVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== SUPORTE (TICKETS) ====================

    @GetMapping("/tickets")
    public List<TicketItem> listarTickets() {
        List<TicketItem> lista = new ArrayList<>();

        for (SupportTicket t : supportTicketRepository.findAll()) {
            List<MensagemItem> mensagens = new ArrayList<>();
            if (t.getMessage() != null && !t.getMessage().isBlank()) {
                mensagens.add(new MensagemItem(
                        t.getUsuario() != null ? t.getUsuario().getNome() : "Usuário",
                        t.getMessage()
                ));
            }

            lista.add(new TicketItem(
                    t.getId(),
                    t.getSubject(),
                    t.getUsuario() != null ? t.getUsuario().getNome() : "Usuário",
                    "Geral",
                    "Normal",
                    "FECHADO".equalsIgnoreCase(t.getStatus()) ? "Resolvido" : "Aberto",
                    t.getCreatedAt() != null ? t.getCreatedAt().format(DATA_FMT) : "—",
                    mensagens
            ));
        }
        return lista;
    }

    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<?> atualizarStatusTicket(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String novoStatus = body.getOrDefault("status", "ABERTO");
        String statusBanco = "Resolvido".equalsIgnoreCase(novoStatus) ? "FECHADO" : "ABERTO";

        return supportTicketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(statusBanco);
                    supportTicketRepository.save(ticket);
                    return ResponseEntity.ok(Map.of("id", ticket.getId(), "status", novoStatus));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String formatarStatusVideo(String status) {
        if (status == null) return "Em análise";
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "CONCLUIDO" -> "Concluído";
            case "ERRO" -> "Erro";
            default -> "Em análise";
        };
    }
}