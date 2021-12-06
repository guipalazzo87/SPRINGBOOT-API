package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<Usuario> findByCpf(String cpf);
}
