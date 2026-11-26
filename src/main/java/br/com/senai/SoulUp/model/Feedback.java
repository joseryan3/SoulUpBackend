package br.com.senai.SoulUp.model;

import jakarta.persistence.*;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private AnalysisResult analysisResult; // Ligado ao resultado da IA

    @Column(columnDefinition = "TEXT")
    private String detailedComment; // Comentários textuais

    private String visualMarkers; // JSON com timestamps e coordenadas (ex: "00:12: Joelho desalinhado")

    private String correctionTip; // Sugestão específica de correção

    // Getters e Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(AnalysisResult analysisResult) {
        this.analysisResult = analysisResult;
    }

    public String getDetailedComment() {
        return detailedComment;
    }

    public void setDetailedComment(String detailedComment) {
        this.detailedComment = detailedComment;
    }

    public String getVisualMarkers() {
        return visualMarkers;
    }

    public void setVisualMarkers(String visualMarkers) {
        this.visualMarkers = visualMarkers;
    }

    public String getCorrectionTip() {
        return correctionTip;
    }

    public void setCorrectionTip(String correctionTip) {
        this.correctionTip = correctionTip;
    }
}