package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    // Permite que o usuário veja seus próprios chamados
    List<SupportTicket> findByUsuarioId(Long usuarioId);
}