package dev.guipalazzo.spring.api.exception;

import dev.guipalazzo.spring.api.domain.StatusPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservaNaoEstaNoStatusEsperadoException extends RuntimeException {
    public ReservaNaoEstaNoStatusEsperadoException(String acao, StatusPagamento statusPagamento){
        super(String.format("Não é possível realizar o %s para esta reserva, pois ela não está no status %s.", acao, statusPagamento.toString()));
    }
}
