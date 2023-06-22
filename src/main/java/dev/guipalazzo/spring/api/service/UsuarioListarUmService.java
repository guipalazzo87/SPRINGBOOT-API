package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.exception.EmailJaCadastradoException;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import dev.guipalazzo.spring.api.request.AtualizarUsuarioRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioListarUmService {

    final UsuarioRepository usuarioRepository;

    public UsuarioListarUmService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> listarUm(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizar(Long idUsuario, AtualizarUsuarioRequest body) {
        boolean usuarioExiste = usuarioRepository.existsById(idUsuario);
        if (!usuarioExiste) throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), idUsuario);


        Optional<Usuario> optionalUsuario = listarUm(idUsuario);
        Usuario usuarioTemp = optionalUsuario.orElse(null);

        assert usuarioTemp != null;
        if (!usuarioTemp.getEmail().equals(body.getEmail())) {
            boolean emailJaCadastrado = usuarioRepository.existsByEmail(body.getEmail());
            if (emailJaCadastrado) throw new EmailJaCadastradoException(body.getEmail());
        }

        usuarioTemp.setNome(body.getNome());
        usuarioTemp.setEmail(body.getEmail());
        usuarioTemp.setSenha(body.getSenha());
        usuarioTemp.setDataNascimento(body.getDataNascimento());
        usuarioTemp.getEndereco().setCep(body.getEndereco().getCep());
        usuarioTemp.getEndereco().setLogradouro(body.getEndereco().getLogradouro());
        usuarioTemp.getEndereco().setNumero(body.getEndereco().getNumero());
        usuarioTemp.getEndereco().setComplemento(body.getEndereco().getComplemento());
        usuarioTemp.getEndereco().setBairro(body.getEndereco().getBairro());
        usuarioTemp.getEndereco().setCidade(body.getEndereco().getCidade());
        usuarioTemp.getEndereco().setEstado(body.getEndereco().getEstado());

        return usuarioRepository.save(usuarioTemp);
    }
}
