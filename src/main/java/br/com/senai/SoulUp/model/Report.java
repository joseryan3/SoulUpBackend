package br.com.senai.SoulUp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Quem denunciou

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // Se a denúncia for de um post

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment; // Se a denúncia for de um comentário

    @Column(nullable = false)
    private String reason; // Motivo da denúncia (ex: "Ofensivo", "Spam")

    private String status = "PENDENTE"; // PENDENTE, ANALISADO, REJEITADO (Prepara a RGN20)

    private LocalDateTime createdAt = LocalDateTime.now();

    // Gerar Getters e Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}