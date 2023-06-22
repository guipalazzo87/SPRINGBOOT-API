package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.exception.ReservaNaoEstaNoStatusEsperadoException;
import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.domain.Pagamento;
import dev.guipalazzo.spring.api.domain.Reserva;
import dev.guipalazzo.spring.api.domain.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservaCancelarReservaService {
    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaCancelarReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void execute(Long idReserva) {
        Optional<Reserva> optionalReserva = reservaRepository.findByIdAndAnuncioAtivoTrue(idReserva);
        if (optionalReserva.isEmpty())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PENDENTE))
            throw new ReservaNaoEstaNoStatusEsperadoException("cancelamento", StatusPagamento.PENDENTE);

        Pagamento pagamento = reserva.getPagamento();

        pagamento.setStatus(StatusPagamento.CANCELADO);

        reserva.setPagamento(pagamento);
        reserva.setAtivo(false);

        reservaRepository.save(reserva);
    }
}
