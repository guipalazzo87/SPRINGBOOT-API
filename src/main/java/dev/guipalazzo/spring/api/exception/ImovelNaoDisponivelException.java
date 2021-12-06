package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImovelNaoDisponivelException extends RuntimeException {
    public ImovelNaoDisponivelException() {
        super("Este anuncio já esta reservado para o período informado.");
    }
}
