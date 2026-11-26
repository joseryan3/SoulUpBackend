package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Busca os posts mais recentes para o Feed
    // RGN21: Busca todos os posts ordenando do mais recente para o mais antigo
    List<Post> findAllByOrderByCreatedAtDesc();
}