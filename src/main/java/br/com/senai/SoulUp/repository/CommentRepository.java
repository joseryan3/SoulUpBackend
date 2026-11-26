package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Adicione o underline (_) para separar explicitamente o critério de busca
    List<Comment> findByVideoId(Long videoId);
}