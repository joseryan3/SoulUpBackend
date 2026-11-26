package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    // Busca o progresso pelo ID do usuário (Usado no GamificationService)
    Optional<UserProgress> findByUsuarioId(Integer usuarioId);

    // Busca os 10 usuários com mais pontos para o Ranking (RF16)
    // O Spring Data JPA monta o SQL automaticamente baseado no nome do método!
    List<UserProgress> findTop10ByOrderByTotalPointsDesc();
}