package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.DTO.UserStatsDTO; // mude para 'dto' minúsculo se necessário
import br.com.senai.SoulUp.repository.AnalysisResultRepository;
import br.com.senai.SoulUp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")

public class StatsController {

    @Autowired
    private VideoRepository videoRepository;





    @Autowired
    private AnalysisResultRepository analysisRepository;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UserStatsDTO> getStats(@PathVariable Long usuarioId) {
        UserStatsDTO stats = new UserStatsDTO();

        // 1. Total de vídeos
        var videos = videoRepository.findByUsuarioId(usuarioId);
        stats.setTotalVideos(videos.size());

        // 2. Média de precisão (Lógica simples)
        // Buscamos as análises vinculadas aos vídeos desse usuário
        double soma = 0;
        int contagem = 0;

        for (var video : videos) {
            var result = analysisRepository.findByVideoId(video.getId());
            if (result != null && result.getAccuracyScore() != null) {
                soma += result.getAccuracyScore();
                contagem++;
            }
        }

        if (contagem > 0) {
            stats.setAverageAccuracy(soma / contagem);
        } else {
            stats.setAverageAccuracy(0.0);
        }

        return ResponseEntity.ok(stats);
    }
}
