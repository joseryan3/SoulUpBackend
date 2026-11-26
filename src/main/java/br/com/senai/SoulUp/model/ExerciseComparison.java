package br.com.senai.SoulUp.model;

// IMPORTS ESSENCIAIS QUE ESTAVAM FALTANDO:
import java.util.List;


public class ExerciseComparison {
    private String exerciseName;
    private List<JointAngle> userAngles; // Ângulos calculados do usuário
    private List<JointAngle> idealAngles; // Ângulos de referência
    private Double romPercentage; // Amplitude de movimento (Range of Motion)

    // Getters e Setters
    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public List<JointAngle> getUserAngles() {
        return userAngles;
    }

    public void setUserAngles(List<JointAngle> userAngles) {
        this.userAngles = userAngles;
    }

    public List<JointAngle> getIdealAngles() {
        return idealAngles;
    }

    public void setIdealAngles(List<JointAngle> idealAngles) {
        this.idealAngles = idealAngles;
    }

    public Double getRomPercentage() {
        return romPercentage;
    }

    public void setRomPercentage(Double romPercentage) {
        this.romPercentage = romPercentage;
    }
}