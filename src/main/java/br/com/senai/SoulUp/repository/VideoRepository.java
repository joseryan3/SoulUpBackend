package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    // Aqui você poderá adicionar buscas personalizadas no futuro, como:
    // List<Video> findByUsuarioId(Long usuarioId);
        List<Video> findByUsuarioId(Long usuarioId);
        java.util.Optional<Video> findByFileName(String fileName);
        // Busca por tipo de exercício (RF12)
        List<Video> findByTipoExercicioIgnoreCase(String tipoExercicio);

        // Busca por nível de dificuldade
        List<Video> findByNivelDificuldade(String nivel);

        // Busca entre datas específicas
        List<Video> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    }
