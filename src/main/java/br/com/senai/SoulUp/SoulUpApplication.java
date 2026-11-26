package br.com.senai.SoulUp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// Classe principal do projeto
@SpringBootApplication
@EnableAsync
public class SoulUpApplication {
	public static void main(String[] args) {
// Inicializa o servidor
		SpringApplication.run(SoulUpApplication.class, args);

	}
}