package br.com.senai.SoulUp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens") // Garante que bate com o nome no SQL
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Use Integer para bater com o INT do seu SQL

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "usuario_id") // Nome da coluna no seu SQL
    private Usuario usuario; // Use a sua classe Usuario, não a do Apache!

    @Column(name = "data_expiracao")
    private LocalDateTime expiryDate;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.expiryDate = LocalDateTime.now().plusMinutes(30);
    }

    // Getters fundamentais para o PasswordService não dar erro:
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getExpiryDate() { return expiryDate; }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
