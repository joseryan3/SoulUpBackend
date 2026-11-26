package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUsuarioId(Integer usuarioId);
}