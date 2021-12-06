package dev.guipalazzo.spring.api.repository;

import dev.guipalazzo.spring.api.domain.Imovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ImovelRepository extends PagingAndSortingRepository<Imovel, Long> {

    Page<Imovel> findByProprietarioId(long idProprietario, Pageable paging);

}
