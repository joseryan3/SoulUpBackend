package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.ExerciseComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComparisonService {

    @Autowired
    private GeminiService geminiService; // Serviço para chamar a API do Google

    public String analyzeExecution(ExerciseComparison data) {
        // 1. Lógica de Alinhamento: Compara ângulos (ex: Joelho no agachamento)
        // 2. Lógica de Amplitude: Verifica se desceu o suficiente

        // Criamos o "Prompt" para a IA
        String prompt = String.format(
                "Analise este exercício de %s. O usuário atingiu %s de amplitude. " +
                        "Os ângulos das articulações foram: %s. Gere dicas curtas de correção.",
                data.getExerciseName(), data.getRomPercentage(), data.getUserAngles()
        );

        return geminiService.generateFeedback(prompt);
    }
}