package br.com.senai.SoulUp.model;

public class JointAngle {
    private String jointName; // Nome da articulação (ex: "Knee", "Elbow")
    private Double angleValue; // Valor do ângulo em graus (ex: 180.0)

    // Construtor vazio (padrão)
    public JointAngle() {
    }

    // Construtor cheio para facilitar a criação
    public JointAngle(String jointName, Double angleValue) {
        this.jointName = jointName;
        this.angleValue = angleValue;
    }

    // Getters e Setters

    public String getJointName() {
        return jointName;
    }

    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    public Double getAngleValue() {
        return angleValue;
    }

    public void setAngleValue(Double angleValue) {
        this.angleValue = angleValue;
    }
}