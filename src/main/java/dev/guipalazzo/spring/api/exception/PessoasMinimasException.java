package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PessoasMinimasException extends RuntimeException {
    public PessoasMinimasException(long pessoas, String tipo) {
        super(String.format("Não é possivel realizar uma reserva com menos de %d pessoas para imóveis do tipo %s",
                pessoas,
                StringUtils.capitalize(tipo.toLowerCase())));
    }
}
