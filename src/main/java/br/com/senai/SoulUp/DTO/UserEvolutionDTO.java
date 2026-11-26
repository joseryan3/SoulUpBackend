package br.com.senai.SoulUp.DTO;

import java.util.Map;

public class UserEvolutionDTO {
    private Double currentAverageScore;
    private Double previousAverageScore;
    private Double improvementPercentage;
    private Map<String, Double> progressByExercise; // Ex: "Agachamento" -> 85.0

    // Getters e Setters


    public Double getCurrentAverageScore() {
        return currentAverageScore;
    }

    public void setCurrentAverageScore(Double currentAverageScore) {
        this.currentAverageScore = currentAverageScore;
    }

    public Double getPreviousAverageScore() {
        return previousAverageScore;
    }

    public void setPreviousAverageScore(Double previousAverageScore) {
        this.previousAverageScore = previousAverageScore;
    }

    public Double getImprovementPercentage() {
        return improvementPercentage;
    }

    public void setImprovementPercentage(Double improvementPercentage) {
        this.improvementPercentage = improvementPercentage;
    }

    public Map<String, Double> getProgressByExercise() {
        return progressByExercise;
    }

    public void setProgressByExercise(Map<String, Double> progressByExercise) {
        this.progressByExercise = progressByExercise;
    }
}