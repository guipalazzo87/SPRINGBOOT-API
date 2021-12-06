package dev.guipalazzo.spring.api.exception;

import dev.guipalazzo.spring.api.domain.FormaPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservaNaoAceitaFormaException extends RuntimeException {
    public ReservaNaoAceitaFormaException(List<FormaPagamento> formasAceitas, FormaPagamento formaPagamento) {
        super(String.format("O anúncio não aceita %s como forma de pagamento. As formas aceitas são %s.",
                formaPagamento.toString(),
                Arrays.toString(formasAceitas.toArray())
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim())
        );

    }
}
