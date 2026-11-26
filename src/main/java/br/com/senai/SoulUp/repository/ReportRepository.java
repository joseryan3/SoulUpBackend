package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Busca denúncias pendentes para a moderação analisar depois (RGN20)
    List<Report> findByStatus(String status);
}