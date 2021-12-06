package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NenhumUsuarioEncontradoPorCpfException extends RuntimeException{
    public NenhumUsuarioEncontradoPorCpfException(String cpf) {
        super(String.format("Nenhum(a) Usuario com CPF com o valor '%s' foi encontrado.", cpf));
    }
}
