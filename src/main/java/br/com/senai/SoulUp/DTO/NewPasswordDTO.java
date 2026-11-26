package br.com.senai.SoulUp.DTO;



import br.com.senai.SoulUp.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public class NewPasswordDTO {

    @NotBlank(message = "O token é obrigatório")
    private String token;

    @ValidPassword // Reaproveitando a regra de negócio 01! ✨
    private String newPassword;

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}