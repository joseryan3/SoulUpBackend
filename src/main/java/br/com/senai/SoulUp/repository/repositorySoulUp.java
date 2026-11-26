package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.SoulUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Interface de acesso ao banco

@Repository
public interface repositorySoulUp  extends JpaRepository<SoulUp, Long> {
// Métodos prontos:
// save(), findAll(), deleteById()
}