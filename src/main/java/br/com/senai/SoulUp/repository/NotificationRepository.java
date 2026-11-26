package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
}