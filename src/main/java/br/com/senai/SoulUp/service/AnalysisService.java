package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.DTO.VideoAnalysisResult;
import br.com.senai.SoulUp.model.AnalysisResult;
import br.com.senai.SoulUp.model.Video;
import br.com.senai.SoulUp.repository.AnalysisResultRepository;
import br.com.senai.SoulUp.repository.VideoRepository;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    @Autowired
    private AnalysisResultRepository repository;

    @Autowired
    private VideoRepository videoRepository; // Para RGN09

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PythonAnalysisService pythonAnalysisService;

    @Async
    public void processVideo(Video video) {
        logger.info("Iniciando processamento do video ID: {}", video.getId());

        try {
            // RGN09: Atualiza status para PROCESSANDO
            video.setStatus("PROCESSANDO");
            videoRepository.save(video);

            // 1. Analise real do video (MediaPipe): repeticoes, angulo do joelho, pontuacao
            VideoAnalysisResult analysis = pythonAnalysisService.analyze(Path.of(video.getFilePath()));
            double accuracy = analysis.accuracy() != null ? analysis.accuracy() : 0.0;

            // 2. RGN13 e RGN14: o Gemini transforma as metricas medidas em dicas de correcao.
            // Se ele falhar, a analise do video continua valida: usamos um texto local.
            String suggestions;
            try {
                suggestions = geminiService.generateFeedback(buildPrompt(analysis, accuracy));
            } catch (Exception geminiError) {
                logger.warn("Gemini indisponivel para o video {}: {}", video.getId(), geminiError.getMessage());
                suggestions = fallbackSuggestions(analysis);
            }

            // 3. RGN12: salva o resultado (reaproveita a linha existente se o video for reanalisado)
            AnalysisResult result = repository.findByVideoId(video.getId());
            if (result == null) {
                result = new AnalysisResult();
                result.setVideo(video);
            }
            result.setProcessedAt(LocalDateTime.now());
            result.setSuggestions(suggestions);
            result.setAccuracyScore(accuracy);
            result.setRepetitions(analysis.repetitions());
            repository.save(result);

            // RGN09: Atualiza status para CONCLUIDO
            video.setScore(accuracy);
            video.setTipoExercicio(analysis.exercise());
            video.setStatus("CONCLUIDO");
            videoRepository.save(video);

            logger.info("Analise concluida para o video {} (reps={}, precisao={}).",
                    video.getFileName(), analysis.repetitions(), accuracy);

        } catch (Exception e) {
            // RGN09: Em caso de falha, marca como ERRO e guarda o motivo para o front mostrar
            logger.error("ERRO no processamento do video {}: {}", video.getId(), e.getMessage(), e);
            saveFailure(video, e);
        }
    }

    private String buildPrompt(VideoAnalysisResult analysis, double accuracy) {
        String angle = analysis.minKneeAngle() != null
                ? String.format(Locale.US, "%.0f graus", analysis.minKneeAngle())
                : "nao medido";

        return "Voce e um personal trainer. Responda em portugues do Brasil, em no maximo 4 frases curtas, "
                + "sem markdown. Analise tecnica de exercicio: " + analysis.exercise() + ". "
                + "Dados medidos por visao computacional: "
                + "repeticoes completas = " + analysis.repetitions() + "; "
                + "duracao do video = " + analysis.duration() + "s; "
                + "menor angulo do joelho = " + angle + " (ideal: cerca de 90 graus); "
                + "precisao do movimento = " + String.format(Locale.US, "%.0f", accuracy) + "%; "
                + "observacao do sistema = " + analysis.feedback() + ". "
                + "Com base na RGN14, classifique o principal problema em uma destas categorias: "
                + "POSTURA INCORRETA, AMPLITUDE INSUFICIENTE, DESEQUILIBRIO ou ALINHAMENTO INADEQUADO "
                + "(ou diga que a execucao esta boa) e defina a severidade como LEVE, MODERADO ou GRAVE. "
                + "Termine com uma recomendacao objetiva de correcao.";
    }

    private String fallbackSuggestions(VideoAnalysisResult analysis) {
        return "Repeticoes completas: " + analysis.repetitions() + ". "
                + (analysis.feedback() != null ? analysis.feedback() : "")
                + " (Dicas detalhadas da IA indisponiveis no momento.)";
    }

    private void saveFailure(Video video, Exception error) {
        try {
            AnalysisResult result = repository.findByVideoId(video.getId());
            if (result == null) {
                result = new AnalysisResult();
                result.setVideo(video);
            }
            result.setProcessedAt(LocalDateTime.now());
            result.setSuggestions("Nao foi possivel analisar o video: " + error.getMessage());
            result.setAccuracyScore(null);
            repository.save(result);

            video.setStatus("ERRO");
            videoRepository.save(video);
        } catch (Exception secondary) {
            logger.error("Falha ao registrar o erro do video {}: {}", video.getId(), secondary.getMessage());
        }
    }
}