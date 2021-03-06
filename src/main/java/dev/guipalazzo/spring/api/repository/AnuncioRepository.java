package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Anuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AnuncioRepository extends PagingAndSortingRepository<Anuncio, Long> {

    Page<Anuncio> findByAnuncianteIdAndAtivoTrue(Long idAnunciante, Pageable paging);

    @Query("select a from Anuncio a where a.imovel.id = ?1 and a.ativo = true")
    List<Anuncio> findAllByImovelIdAndAtivo(Long id);

    @Query("select a from Anuncio a where a.id = ?1 and a.ativo = true")
    Optional<Anuncio> findByIdAndAtivo(Long idAnuncio);

    Page<Anuncio> findByAtivoTrue(Pageable paging);
}
