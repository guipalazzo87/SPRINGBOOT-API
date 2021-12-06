package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JaExisteAnuncioException extends RuntimeException {
    public JaExisteAnuncioException(Long id) {
        super(String.format("Já existe um recurso do tipo Anuncio com IdImovel com o valor '%d'.", id));
    }
}
