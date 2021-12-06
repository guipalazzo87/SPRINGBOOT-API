package dev.guipalazzo.spring.api.request;

import dev.guipalazzo.spring.api.domain.Periodo;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Component
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CadastrarReservaRequest {

    @NotNull
    private Long idSolicitante;

    @NotNull
    private Long idAnuncio;

    @NotNull
    @Valid
    private Periodo periodo;

    @NotNull
    private Integer quantidadePessoas;

}
