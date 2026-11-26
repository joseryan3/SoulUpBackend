package br.com.senai.SoulUp.repository;

import br.com.senai.SoulUp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByUsuario(String usuario);

    default Optional<Usuario> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return findById(Math.toIntExact(id));
    }
}
