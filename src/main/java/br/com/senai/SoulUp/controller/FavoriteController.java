package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.Favorite;
import br.com.senai.SoulUp.model.Video;
import br.com.senai.SoulUp.repository.FavoriteRepository;
import br.com.senai.SoulUp.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private VideoRepository videoRepository;

    // Favoritar um vídeo
    @PostMapping("/{videoId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long videoId, @RequestParam(required = false) String category) {
        // Simulando usuário logado ID 1 (Você integrará com seu Security depois)
        Long mockUserId = 1L;

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado"));

        Favorite favorite = new Favorite();
        favorite.setVideo(video);
        // favorite.setUsuario(usuarioLogado);
        favorite.setCategory(category != null ? category : "Geral");

        favoriteRepository.save(favorite);
        return ResponseEntity.ok("Vídeo adicionado aos favoritos!");
    }

    // Listar favoritos do usuário
    @GetMapping
    public ResponseEntity<List<Favorite>> getMyFavorites() {
        return ResponseEntity.ok(favoriteRepository.findAll());
    }
}
