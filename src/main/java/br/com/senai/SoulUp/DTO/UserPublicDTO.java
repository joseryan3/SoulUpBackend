package br.com.senai.SoulUp.DTO;

public class UserPublicDTO {
    private String nome;
    private String imagemPerfil;
    // Não incluímos email ou outros dados sensíveis aqui

    public UserPublicDTO(String nome, String imagemPerfil) {
        this.nome = nome;
        this.imagemPerfil = imagemPerfil;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagemPerfil() {
        return imagemPerfil;
    }

    public void setImagemPerfil(String imagemPerfil) {
        this.imagemPerfil = imagemPerfil;
    }
}