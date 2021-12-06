package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DatasInvalidasException extends RuntimeException {
    public DatasInvalidasException() {
        super("Período inválido! A data final da reserva precisa ser maior do que a data inicial.");
    }
}

