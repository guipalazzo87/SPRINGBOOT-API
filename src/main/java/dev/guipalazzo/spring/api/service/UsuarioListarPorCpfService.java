package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import dev.guipalazzo.spring.api.domain.Usuario;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioListarPorCpfService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioListarPorCpfService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> listarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

}
