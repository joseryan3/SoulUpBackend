package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Challenge;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private GamificationService gamificationService;

    // Listar desafios ativos para o usuário ver no App
    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findAll();
    }

    // Método chamado quando o usuário termina um treino (RF19)
    public void checkChallengeCompletion(Integer usuarioId, String tipoExercicio) {
        // Exemplo: Se o exercício for o do desafio, dá os pontos extras
        List<Challenge> active = getActiveChallenges();

        for (Challenge c : active) {
            if (c.getTitle().contains(tipoExercicio)) {
                gamificationService.addPoints(usuarioId, c.getPointsReward());
                System.out.println("Desafio Concluído! Pontos extras adicionados.");
            }
        }
    }
}