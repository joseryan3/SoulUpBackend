package br.com.senai.SoulUp.service;


import br.com.senai.SoulUp.DTO.UserEvolutionDTO;
import br.com.senai.SoulUp.model.AnalysisResult;
import br.com.senai.SoulUp.repository.AnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    @Autowired
    private AnalysisResultRepository analysisRepository;

    public UserEvolutionDTO calculateEvolution(Long usuarioId) {
        // Busca o histórico (RGN12/15)
        List<AnalysisResult> historico = analysisRepository.findByVideoUsuarioIdOrderByProcessedAtDesc(usuarioId);

        if (historico.size() < 2) return new UserEvolutionDTO(); // Precisa de pelo menos 2 para comparar

        // Média das últimas 5 análises
        Double mediaAtual = historico.stream().limit(5)
                .mapToDouble(AnalysisResult::getAccuracyScore).average().orElse(0.0);

        // Média das 5 análises anteriores a essas
        Double mediaAntiga = historico.stream().skip(5).limit(5)
                .mapToDouble(AnalysisResult::getAccuracyScore).average().orElse(0.0);

        UserEvolutionDTO evolution = new UserEvolutionDTO();
        evolution.setCurrentAverageScore(mediaAtual);
        evolution.setPreviousAverageScore(mediaAntiga);

        // Cálculo de melhoria (RGN16)
        if (mediaAntiga > 0) {
            evolution.setImprovementPercentage(((mediaAtual - mediaAntiga) / mediaAntiga) * 100);
        }

        return evolution;
    }
}