package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Usuario;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmail(String email);

    @NotNull Usuario save(@NotNull Usuario usuario);
}
