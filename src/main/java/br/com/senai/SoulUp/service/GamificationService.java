package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.UserProgress;
import br.com.senai.SoulUp.repository.UserProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GamificationService {

    @Autowired
    private UserProgressRepository progressRepository;

    @Transactional
    public void addPoints(Integer usuarioId, Integer points) {
        UserProgress progress = progressRepository.findByUsuarioId(usuarioId)
                .orElse(new UserProgress()); // Cria se não existir

        progress.setTotalPoints(progress.getTotalPoints() + points);

        // Lógica simples de Level Up (cada 1000 pontos sobe um nível)
        progress.setLevel((progress.getTotalPoints() / 1000) + 1);

        progressRepository.save(progress);
    }
}