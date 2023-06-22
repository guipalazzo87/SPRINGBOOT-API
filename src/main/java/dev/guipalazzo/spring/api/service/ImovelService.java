package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.ImovelComAnuncioException;
import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.repository.ImovelRepository;
import dev.guipalazzo.spring.api.domain.Anuncio;
import dev.guipalazzo.spring.api.domain.Imovel;
import dev.guipalazzo.spring.api.domain.Usuario;
import dev.guipalazzo.spring.api.request.CadastrarImovelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImovelService {

    private final ImovelRepository imovelRepository;
    private final AnuncioListarPorImovelService anuncioListarPorImovelService;
    private final UsuarioListarUmService usuarioListarUmService;

    @Autowired
    public ImovelService(ImovelRepository imovelRepository,
                         AnuncioListarPorImovelService anuncioListarPorImovelService, UsuarioListarUmService usuarioListarUmService) {
        this.imovelRepository = imovelRepository;
        this.anuncioListarPorImovelService = anuncioListarPorImovelService;
        this.usuarioListarUmService = usuarioListarUmService;
    }

    public Imovel salvar(CadastrarImovelRequest body) {
        Optional<Usuario> optionalUsuario = usuarioListarUmService.listarUm(body.getIdProprietario());

        if (optionalUsuario.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Usuario.class.getSimpleName(), body.getIdProprietario());
        Usuario proprietario = optionalUsuario.orElse(null);

        Imovel imovel = Imovel.builder()
                .identificacao(body.getIdentificacao())
                .tipoImovel(body.getTipoImovel())
                .endereco(body.getEndereco())
                .proprietario(proprietario)
                .caracteristicas(body.getCaracteristicas())
                .ativo(true)
                .build();
        return imovelRepository.save(imovel);
    }

    public Page<Imovel> listarTodos(Integer page,
                                    Integer size,
                                    List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));

        return imovelRepository.findAll(paging);
    }

    public Page<Imovel> listarPorProprietario(Integer page,
                                              Integer size,
                                              List<Sort.Order> ordenacao,
                                              Long idProprietario) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return imovelRepository.findByProprietarioId(idProprietario, paging);
    }

    public Optional<Imovel> listarImovelPorId(Long idImovel) {
        return imovelRepository.findById(idImovel);
    }

    public void deletarImovel(Long idImovel) {
        Optional<Imovel> optionalImovel = listarImovelPorId(idImovel);
        Imovel imovel = optionalImovel.orElse(null);

        List<Anuncio> anuncioList = anuncioListarPorImovelService.listarPorImovel(idImovel);
        if (!anuncioList.isEmpty()) throw new ImovelComAnuncioException();

        if (imovel != null) {
            Imovel novoImovel = new Imovel(
                    imovel.getId(),
                    imovel.getIdentificacao(),
                    imovel.getTipoImovel(),
                    imovel.getEndereco(),
                    imovel.getProprietario(),
                    imovel.getCaracteristicas(),
                    false
            );
            imovelRepository.save(novoImovel);
        } else {
            throw new ObjetoNaoEncontradoPorIdException(Imovel.class.getSimpleName(), idImovel);
        }
    }
}
