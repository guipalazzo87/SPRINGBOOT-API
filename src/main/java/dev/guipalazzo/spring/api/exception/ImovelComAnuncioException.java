package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImovelComAnuncioException extends RuntimeException {
    public ImovelComAnuncioException() {
        super("Não é possível excluir um imóvel que possua um anúncio.");
    }
}
