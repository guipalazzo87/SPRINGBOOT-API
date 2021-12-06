package dev.guipalazzo.spring.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DiariasMinimasException extends RuntimeException {
    public DiariasMinimasException(long diarias, String tipo) {
        super(String.format("Não é possivel realizar uma reserva com menos de %d diárias para imóveis do tipo %s",
                diarias,
                StringUtils.capitalize(tipo.toLowerCase())));
    }
}
