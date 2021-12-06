package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjetoNaoEncontradoPorIdException extends RuntimeException {
    public ObjetoNaoEncontradoPorIdException(String objeto, Long idImovel) {
        super(String.format("Nenhum(a) %s com Id com o valor '%d' foi encontrado.", objeto, idImovel));
    }
}
