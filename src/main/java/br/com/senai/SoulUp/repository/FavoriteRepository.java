package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUsuarioId(Long usuarioId);

    // Para verificar se já está favoritado antes de salvar
    Optional<Favorite> findByUsuarioIdAndVideoId(Long usuarioId, Long videoId);
}