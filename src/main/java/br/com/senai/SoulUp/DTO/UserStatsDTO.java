package br.com.senai.SoulUp.DTO;


public class UserStatsDTO {

    private int totalVideos;
    private double averageAccuracy; // Atributo para armazenar a média de precisão

    // Construtor padrão
    public UserStatsDTO() {
    }

    // Getter e Setter para TotalVideos
    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    // Getter e Setter para AverageAccuracy (O método que o controller estava cobrando!)
    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }
}