package dev.guipalazzo.spring.api.service;

import dev.guipalazzo.spring.api.exception.ObjetoNaoEncontradoPorIdException;
import dev.guipalazzo.spring.api.exception.ReservaNaoAceitaFormaException;
import dev.guipalazzo.spring.api.exception.ReservaNaoEstaNoStatusEsperadoException;
import dev.guipalazzo.spring.api.repository.ReservaRepository;
import dev.guipalazzo.spring.api.domain.FormaPagamento;
import dev.guipalazzo.spring.api.domain.Pagamento;
import dev.guipalazzo.spring.api.domain.Reserva;
import dev.guipalazzo.spring.api.domain.StatusPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservaPagarReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaPagarReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void execute(Long idReserva, FormaPagamento formaPagamento) {

        Optional<Reserva> optionalReserva = reservaRepository.findByIdAndAnuncioAtivoTrue(idReserva);
        if (!optionalReserva.isPresent())
            throw new ObjetoNaoEncontradoPorIdException(Reserva.class.getSimpleName(), idReserva);
        Reserva reserva = optionalReserva.orElse(null);

        if (!reserva.getAnuncio().getFormasAceitas().contains(formaPagamento))
            throw new ReservaNaoAceitaFormaException(reserva.getAnuncio().getFormasAceitas(), formaPagamento);

        if (!reserva.getPagamento().getStatus().equals(StatusPagamento.PENDENTE))
            throw new ReservaNaoEstaNoStatusEsperadoException("pagamento", StatusPagamento.PENDENTE);

        Pagamento pagamento = new Pagamento(
                reserva.getPagamento().getValorTotal(),
                formaPagamento,
                StatusPagamento.PAGO
                );
        reservaRepository.save(new Reserva(
                reserva.getId(),
                reserva.getSolicitante(),
                reserva.getAnuncio(),
                reserva.getPeriodo(),
                reserva.getQuantidadePessoas(),
                reserva.getDataHoraReserva(),
                pagamento,
                true
        ));
    }
}
