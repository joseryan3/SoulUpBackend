package br.com.senai.SoulUp.controller;


import br.com.senai.SoulUp.model.SoulUp;
import br.com.senai.SoulUp.repository.repositorySoulUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

// Define como API REST
@RestController
// URL base
@RequestMapping("/api/home")

public class controllerSoulUp {

    @PostMapping
    public String login(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        String senha = dados.get("senha");

        if(email.equals("admin") && senha.equals("123")) {
            return "Login OK";
        }

        return "Erro";
    }
}
