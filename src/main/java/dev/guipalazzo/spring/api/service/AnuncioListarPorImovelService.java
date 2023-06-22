package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.repository.AnuncioRepository;
import dev.guipalazzo.spring.api.domain.Anuncio;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnuncioListarPorImovelService {

    private final @NonNull AnuncioRepository anuncioRepository ;

    public AnuncioListarPorImovelService(@NonNull AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    public List<Anuncio> listarPorImovel(Long idImovel) {
        return anuncioRepository.findAllByImovelIdAndAtivo(idImovel);
    }
}
