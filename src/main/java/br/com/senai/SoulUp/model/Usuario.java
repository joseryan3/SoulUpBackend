package br.com.senai.SoulUp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario", unique = true)
    private String usuario;

    @Column(name = "premium")
    private boolean premium = false;

    @Column(name = "data_expiracao_assinatura")
    private LocalDateTime dataExpiracaoAssinatura;

    @Column(name = "plano_tipo")
    private String planoTipo; // Ex: "MENSAL", "ANUAL", "FREE"

    @Column(name = "ativo")
    private boolean ativo = true;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Column(name = "perfil_publico")
    private boolean perfilPublico = true; // Valor padrão é público

    @Column(name = "imagem_perfil")
    private String imagemPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoUsuario tipo;

    public enum TipoUsuario {
        USUARIO,
        MODERADOR,
        ADMIN
    }

    // --- GETTERS E SETTERS ---

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public LocalDateTime getDataExpiracaoAssinatura() {
        return dataExpiracaoAssinatura;
    }

    public void setDataExpiracaoAssinatura(LocalDateTime dataExpiracaoAssinatura) {
        this.dataExpiracaoAssinatura = dataExpiracaoAssinatura;
    }

    public String getPlanoTipo() {
        return planoTipo;
    }

    public void setPlanoTipo(String planoTipo) {
        this.planoTipo = planoTipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isPerfilPublico() {
        return perfilPublico;
    }

    public void setPerfilPublico(boolean perfilPublico) {
        this.perfilPublico = perfilPublico;
    }

    public String getImagemPerfil() {
        return imagemPerfil;
    }

    public void setImagemPerfil(String imagemPerfil) {
        this.imagemPerfil = imagemPerfil;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
}