package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    // Busca desafios que ainda não venceram
    List<Challenge> findByDeadlineAfter(LocalDateTime now);
}