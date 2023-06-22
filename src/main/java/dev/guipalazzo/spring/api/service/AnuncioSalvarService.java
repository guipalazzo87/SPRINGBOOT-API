package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.JaExisteAnuncioException;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.repository.AnuncioRepository;
import dev.guipalazzo.spring.api.domain.Anuncio;
import dev.guipalazzo.spring.api.domain.Imovel;
import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.request.CadastrarAnuncioRequest;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnuncioSalvarService {

    private final @NonNull AnuncioListarPorImovelService anuncioListarPorImovelService;
    private final @NonNull ImovelService imovelService;
    private final @NonNull AnuncioRepository anuncioRepository;
    private final UsuarioListarUmService usuarioListarUmService;

    public AnuncioSalvarService(@NonNull AnuncioListarPorImovelService anuncioListarPorImovelService, @NonNull UsuarioListarPorCpfService usuarioListarPorCpfService, @NonNull ImovelService imovelService, @NonNull AnuncioRepository anuncioRepository, UsuarioListarUmService usuarioListarUmService) {
        this.anuncioListarPorImovelService = anuncioListarPorImovelService;
        this.imovelService = imovelService;
        this.anuncioRepository = anuncioRepository;
        this.usuarioListarUmService = usuarioListarUmService;
    }

    public Anuncio salvar(CadastrarAnuncioRequest body) {

        Optional<Usuario> optionalUsuario = usuarioListarUmService.listarUm(body.getIdAnunciante());
        if (optionalUsuario.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdAnunciante());
        Usuario anunciante = optionalUsuario.orElse(null);

        Optional<Imovel> optionalImovel = imovelService.listarImovelPorId(body.getIdImovel());
        if (optionalImovel.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), body.getIdImovel());
        Imovel imovel = optionalImovel.orElse(null);

        List<Anuncio> anuncioList = anuncioListarPorImovelService.listarPorImovel(imovel.getId());
        if (!anuncioList.isEmpty()) throw new JaExisteAnuncioException(imovel.getId());

        Anuncio anuncio = new Anuncio(
                body.getTipoAnuncio(),
                imovel,
                anunciante,
                body.getValorDiaria(),
                body.getFormasAceitas(),
                body.getDescricao(),
                true
        );
        return anuncioRepository.save(anuncio);
    }

}
