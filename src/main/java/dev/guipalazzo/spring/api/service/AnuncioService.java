package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.repository.AnuncioRepository;
import dev.guipalazzo.spring.api.domain.Anuncio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnuncioService {
    private final AnuncioRepository anuncioRepository;

    @Autowired
    public AnuncioService(AnuncioRepository anuncioRepository
    ) {
        this.anuncioRepository = anuncioRepository;
    }


    public Page<Anuncio> listarTodos(Integer page,
                                     Integer size,
                                     List<Sort.Order> ordenacao) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        Page<Anuncio> resultadoPaginado = anuncioRepository.findByAtivoTrue(paging);
        return resultadoPaginado;
    }

    public Page<Anuncio> listarPorAnunciante(Integer page,
                                             Integer size,
                                             List<Sort.Order> ordenacao,
                                             Long idAnunciante) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        Page<Anuncio> resultadoPaginado = anuncioRepository.findByAnuncianteIdAndAtivoTrue(idAnunciante, paging);
        return resultadoPaginado;
    }

    public Optional<Anuncio> listarAnuncioPorId(Long idAnuncio) {
        return anuncioRepository.findByIdAndAtivo(idAnuncio);
    }

    public void deletarAnuncio(Long idAnuncio) {
        Optional<Anuncio> optionalAnuncio = listarAnuncioPorId(idAnuncio);
        Anuncio anuncio = optionalAnuncio.orElse(null);

        if (anuncio != null) {
            Anuncio novoAnuncio = new Anuncio(
                    anuncio.getId(),
                    anuncio.getTipoAnuncio(),
                    anuncio.getImovel(),
                    anuncio.getAnunciante(),
                    anuncio.getValorDiaria(),
                    anuncio.getFormasAceitas(),
                    anuncio.getDescricao(),
                    false
            );
            anuncioRepository.save(novoAnuncio);
        } else {
            throw new ObjetoNaoEncontradoPorIdException(Anuncio.class.getSimpleName(), idAnuncio);
        }
    }
}
