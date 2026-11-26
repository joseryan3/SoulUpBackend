package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUsuarioId(Long usuarioId);
}