package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Buscar avaliação de uma análise específica
    Optional<Rating> findByAnalysisResultId(Long analysisResultId);
}