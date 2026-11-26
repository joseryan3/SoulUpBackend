    package br.com.senai.SoulUp.controller;

    import br.com.senai.SoulUp.model.AnalysisResult;
    import br.com.senai.SoulUp.model.Rating;
    import br.com.senai.SoulUp.repository.AnalysisResultRepository;
    import br.com.senai.SoulUp.repository.RatingRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/ratings")

    public class RatingController {

        @Autowired
        private RatingRepository ratingRepository;

        @Autowired
        private AnalysisResultRepository analysisResultRepository;

        @PostMapping("/analysis/{analysisId}")
        public ResponseEntity<?> createRating(
                @PathVariable Long analysisId,
                @RequestBody Rating rating) {

            AnalysisResult analysis = analysisResultRepository.findById(analysisId)
                    .orElseThrow(() -> new RuntimeException("Análise não encontrada"));

            // Vincula a avaliação à análise
            rating.setAnalysisResult(analysis);

            // Em um cenário real, você setaria o usuário logado aqui
            // rating.setUsuario(currentUser);

            ratingRepository.save(rating);

            return ResponseEntity.status(HttpStatus.CREATED).body("Avaliação enviada com sucesso!");
        }
    }
