package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.exception.ReservaNaoEstaNoStatusEsperadoException;
import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.domain.Reserva;
import dev.guipalazzo.spring.api.domain.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservaEstornarReservaService {
    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaEstornarReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void execute(Long idReserva) {
        Optional<Reserva> optionalReserva = reservaRepository.findByIdAndAnuncioAtivoTrue(idReserva);
        if (optionalReserva.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PAGO))
            throw new ReservaNaoEstaNoStatusEsperadoException("estorno", StatusPagamento.PAGO);

        reserva.getPagamento().setStatus(StatusPagamento.ESTORNADO);
        reserva.getPagamento().setFormaEscolhida(null);
        reserva.setAtivo(false);
        reservaRepository.save(reserva);
    }
}
