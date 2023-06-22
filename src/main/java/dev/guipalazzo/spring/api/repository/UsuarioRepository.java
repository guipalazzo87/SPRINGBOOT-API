package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);

    Usuario save(Usuario usuario);
}
