package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SolicitanteNaoPodeSerAnuncianteException extends RuntimeException {
    public SolicitanteNaoPodeSerAnuncianteException() {
        super("O solicitante de uma reserva não pode ser o próprio anunciante.");
    }
}
