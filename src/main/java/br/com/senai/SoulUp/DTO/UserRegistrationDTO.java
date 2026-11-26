package br.com.senai.SoulUp.DTO;

import br.com.senai.SoulUp.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {


    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "Nome excede o tamanho permitido")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Por favor, insira um e-mail válido")
    @Size(max = 160, message = "E-mail excede o tamanho permitido")
    private String email;

    @ValidPassword
    private String senha;

// ========================================
// GETTERS
// ========================================

    public String getNome() {

        return nome;
    }

    public String getEmail() {

        return email;
    }

    public String getSenha() {

        return senha;
    }

// ========================================
// SETTERS
// ========================================

    public void setNome(String nome) {

        this.nome = nome;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setSenha(String senha) {

        this.senha = senha;
    }


}
