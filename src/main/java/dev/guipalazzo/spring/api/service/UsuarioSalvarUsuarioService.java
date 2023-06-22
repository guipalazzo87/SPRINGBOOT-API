package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.exception.CpfJaCadastradoException;
import dev.guipalazzo.spring.api.exception.EmailJaCadastradoException;
import dev.guipalazzo.spring.api.repository.UsuarioRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class UsuarioSalvarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioSalvarUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario salvar(Usuario usuario) {
        boolean emailJaUtilizado = usuarioRepository.existsByEmail(usuario.getEmail());
        if (emailJaUtilizado)
            throw new EmailJaCadastradoException(usuario.getEmail());

        boolean cpfJaUtilizado = usuarioRepository.existsByCpf(usuario.getCpf());
        if (cpfJaUtilizado) throw new CpfJaCadastradoException(usuario.getCpf());

        ParameterizedTypeReference<String> responseType =
                new ParameterizedTypeReference<>() {
                };

        try {
            RequestEntity<Void> request = RequestEntity.get("https://picsum.photos/200")
                    .accept(MediaType.APPLICATION_JSON).build();
            String urlString = Objects.requireNonNull(new RestTemplate().exchange(request, responseType).getHeaders().get("Picsum-Id")).get(0);
            if (urlString != null) {
                usuario.setAvatarUrl("https://picsum.photos/id/" + urlString + "/200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarioRepository.save(usuario);
    }
}
