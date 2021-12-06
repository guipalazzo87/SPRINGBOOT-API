package dev.guipalazzo.spring.api.request;

import dev.guipalazzo.spring.api.domain.CaracteristicaImovel;
import dev.guipalazzo.spring.api.domain.Endereco;
import dev.guipalazzo.spring.api.domain.TipoImovel;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastrarImovelRequest {

    @NotNull
    private TipoImovel tipoImovel;

    @Valid
    @NotNull
    private Endereco endereco;

    @NotBlank
    private String identificacao;

    @NotNull
    private Long idProprietario;

    private List<CaracteristicaImovel> caracteristicas;
}
