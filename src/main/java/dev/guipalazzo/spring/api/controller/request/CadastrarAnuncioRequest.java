package dev.guipalazzo.spring.api.controller.request;

import dev.guipalazzo.spring.api.domain.FormaPagamento;
import dev.guipalazzo.spring.api.domain.TipoAnuncio;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CadastrarAnuncioRequest {

    @NotNull
    private Long idImovel;

    @NotNull
    private Long idAnunciante;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoAnuncio tipoAnuncio;

    @NotNull
    private BigDecimal valorDiaria;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    private List<FormaPagamento> formasAceitas;

    @NotBlank
    private String descricao;
}
