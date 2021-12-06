package dev.guipalazzo.spring.api.exception;

        import org.springframework.http.HttpStatus;
        import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MinimoUmaDiariaException extends RuntimeException {
    public MinimoUmaDiariaException() {
        super("Período inválido! O número mínimo de diárias precisa ser maior ou igual à 1.");
    }
}

