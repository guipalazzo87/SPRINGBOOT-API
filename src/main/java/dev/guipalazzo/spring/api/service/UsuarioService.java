package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.CpfJaCadastradoException;
import dev.guipalazzo.spring.api.exception.EmailJaCadastradoException;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.request.AtualizarUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository ;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public Usuario salvar(Usuario usuario) {
        boolean emailJaUtilizado = usuarioRepository.existsByEmail(usuario.getEmail());
        if (emailJaUtilizado)
            throw new EmailJaCadastradoException(usuario.getEmail());

        boolean cpfJaUtilizado = usuarioRepository.existsByCpf(usuario.getCpf());
        if (cpfJaUtilizado) throw new CpfJaCadastradoException(usuario.getCpf());

        ParameterizedTypeReference<String> responseType =
                new ParameterizedTypeReference<String>() {};

        try {
            RequestEntity<Void> request = RequestEntity.get("https://picsum.photos/200")
                    .accept(MediaType.APPLICATION_JSON).build();
            String urlString = Objects.requireNonNull(restTemplate.exchange(request, responseType).getHeaders().get("Picsum-Id")).get(0);
            if (urlString != null){
                usuario.setAvatarUrl("https://picsum.photos/id/" + urlString + "/200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> listAll(
            Integer page,
            Integer size,
            List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        Page<Usuario> resultadoPaginado = usuarioRepository.findAll(paging);

        return resultadoPaginado;
    }

    public Optional<Usuario> listarUm(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> listarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
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
