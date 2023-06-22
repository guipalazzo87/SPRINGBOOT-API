package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.domain.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaListarPorAnuncianteService {
    final ReservaRepository reservaRepository;

    @Autowired
    public ReservaListarPorAnuncianteService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Page<Reserva> execute(Integer page, Integer size, List<Sort.Order> ordenacao, Long idAnunciante) {
        Pageable paging = PageRequest.of(page, size, Sort.by(ordenacao));
        return reservaRepository.findByAnuncioAnuncianteIdAndAnuncioAtivoTrue(idAnunciante, paging);
    }


}
