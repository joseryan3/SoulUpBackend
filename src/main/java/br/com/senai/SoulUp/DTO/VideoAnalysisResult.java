package br.com.senai.SoulUp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoAnalysisResult(
        @JsonProperty("exercise")
        String exercise,

        @JsonProperty("repetitions")
        Integer repetitions,

        @JsonProperty("duration")
        Integer duration,

        @JsonProperty("feedback")
        String feedback,

        // Pontuacao 0-100 calculada pelo analisador Python (pode vir nula em versoes antigas do script)
        @JsonProperty("accuracy")
        Double accuracy,

        // Menor angulo de joelho medido no video, em graus (pode vir nulo se nenhuma pose foi detectada)
        @JsonProperty("min_knee_angle")
        Double minKneeAngle
) {

}
