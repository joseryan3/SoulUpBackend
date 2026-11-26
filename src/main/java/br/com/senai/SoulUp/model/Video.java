package br.com.senai.SoulUp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity

@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 1. Campo para o método: findByTipoExercicioIgnoreCase
    @Column(name = "tipo_exercicio")
    private String tipoExercicio;

    // 2. Campo para o método: findByNivelDificuldade
    @Column(name = "nivel_dificuldade")
    private String nivelDificuldade;

    // 3. Campo para o método: findByCreatedAtBetween
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Executa automaticamente antes de salvar o vídeo no banco, preenchendo a data atual
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private String fileName;
    private String contentType; // MP4 ou MOV
    private Long size;
    private String filePath;
    private String status;
    private Double score;



    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Relacionamento com sua classe Usuario

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getTipoExercicio() {
        return tipoExercicio;
    }

    public void setTipoExercicio(String tipoExercicio) {
        this.tipoExercicio = tipoExercicio;
    }

    public String getNivelDificuldade() {
        return nivelDificuldade;
    }

    public void setNivelDificuldade(String nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}