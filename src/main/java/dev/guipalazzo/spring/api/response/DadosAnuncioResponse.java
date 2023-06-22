package dev.guipalazzo.spring.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.guipalazzo.spring.api.domain.FormaPagamento;
import dev.guipalazzo.spring.api.domain.Imovel;
import dev.guipalazzo.spring.api.domain.Usuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@Builder
public class DadosAnuncioResponse {
    @JsonFormat
    private Long id;
    @JsonFormat
    private Imovel imovel;

    @JsonFormat
    private Usuario anunciante;

    @Enumerated(EnumType.STRING)
    private List<FormaPagamento> formasAceitas;
    private String descricao;

    public DadosAnuncioResponse(Long id,
                                Imovel imovel,
                                Usuario anunciante,
                                List<FormaPagamento> formasAceitas,
                                String descricao) {
        this.id = id;
        this.imovel = imovel;
        this.anunciante = anunciante;
        this.formasAceitas = formasAceitas;
        this.descricao = descricao;
    }
}
