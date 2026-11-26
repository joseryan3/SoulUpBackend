package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.UserProgress;
import br.com.senai.SoulUp.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")

public class RankingController {

    @Autowired
    private UserProgressRepository progressRepository;

    @GetMapping("/top")
    public ResponseEntity<List<UserProgress>> getTopUsers() {
        // Busca os 10 usuários com mais pontos
        return ResponseEntity.ok(progressRepository.findTop10ByOrderByTotalPointsDesc());
    }
}
