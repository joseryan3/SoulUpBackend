package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    // Permite buscar a análise de um vídeo específico
    AnalysisResult findByVideoId(Long videoId);

    // RGN12: Para o histórico, buscamos todas as análises de um utilizador através do vídeo
    List<AnalysisResult> findByVideoUsuarioIdOrderByProcessedAtDesc(Long usuarioId);

}