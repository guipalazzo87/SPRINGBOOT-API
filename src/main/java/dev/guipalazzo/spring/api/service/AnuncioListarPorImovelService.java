package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.repository.AnuncioRepository;
import dev.guipalazzo.spring.api.domain.Anuncio;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnuncioListarPorImovelService {

    private final @NonNull AnuncioRepository anuncioRepository ;

    public List<Anuncio> listarPorImovel(Long idImovel) {
        return anuncioRepository.findAllByImovelIdAndAtivo(idImovel);
    }
}
