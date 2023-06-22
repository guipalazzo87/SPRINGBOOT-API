package dev.guipalazzo.spring.api.service;


import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioListarTodosService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioListarTodosService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Usuario> listAll(
            Integer page,
            Integer size,
            List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));

        return usuarioRepository.findAll(paging);
    }
}
