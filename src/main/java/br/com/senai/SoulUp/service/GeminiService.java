package br.com.senai.SoulUp.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final WebClient webClient;

    @Value("${gemini.api.key:}")
    private String apiKey;

    // O modelo antigo "gemini-pro" nao existe mais na API. Deixe configuravel
    // para trocar sem recompilar quando o Google aposentar outro modelo.
    @Value("${gemini.api.model:gemini-3.8-flash}")
    private String model;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    /**
     * Gera o texto de feedback. Em caso de qualquer falha (sem chave, modelo
     * inexistente, quota, rede, resposta vazia) lanca GeminiException com o
     * motivo real, para quem chama decidir o fallback. Nunca devolve uma
     * mensagem de erro disfarcada de feedback.
     */
    public String generateFeedback(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new GeminiException("gemini.api.key nao configurada (defina GEMINI_API_KEY).");
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        Map<?, ?> response;
        try {
            response = webClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(TIMEOUT);
        } catch (WebClientResponseException e) {
            throw new GeminiException("Gemini respondeu HTTP " + e.getStatusCode().value()
                    + " (modelo " + model + "): " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new GeminiException("Falha ao chamar o Gemini (modelo " + model + "): " + e.getMessage(), e);
        }

        String text = extractText(response);
        if (text == null || text.isBlank()) {
            throw new GeminiException("Gemini retornou resposta sem texto (modelo " + model + "): " + response);
        }

        logger.info("Gemini ({}) respondeu com {} caracteres.", model, text.length());
        return text.trim();
    }

    /** Extrai candidates[0].content.parts[*].text da resposta, sem depender de classes do Jackson. */
    private String extractText(Map<?, ?> response) {
        if (response == null || !(response.get("candidates") instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }
        if (!(candidates.get(0) instanceof Map<?, ?> candidate)
                || !(candidate.get("content") instanceof Map<?, ?> content)
                || !(content.get("parts") instanceof List<?> parts)) {
            return null;
        }

        StringBuilder text = new StringBuilder();
        for (Object part : parts) {
            if (part instanceof Map<?, ?> partMap && partMap.get("text") instanceof String value) {
                text.append(value);
            }
        }
        return text.toString();
    }

    public static class GeminiException extends RuntimeException {
        public GeminiException(String message) {
            super(message);
        }

        public GeminiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
