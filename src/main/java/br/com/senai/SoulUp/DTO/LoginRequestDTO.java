package br.com.senai.SoulUp.DTO;

public class LoginRequestDTO {
    private String email;
    private String senha;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha != null ? senha : password;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
