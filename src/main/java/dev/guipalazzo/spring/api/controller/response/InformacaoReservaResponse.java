package dev.guipalazzo.spring.api.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.guipalazzo.spring.api.domain.Pagamento;
import dev.guipalazzo.spring.api.domain.Periodo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class InformacaoReservaResponse {
    @JsonFormat
    private Long idReserva;

    @JsonFormat
    private DadosSolicitanteResponse solicitante;

    @JsonFormat
    private Integer quantidadePessoas;

    @JsonFormat
    private DadosAnuncioResponse anuncio;

    @JsonFormat
    private Periodo periodo;

    @JsonFormat
    private Pagamento pagamento;

    public InformacaoReservaResponse(Long idReserva, DadosSolicitanteResponse solicitante, Integer quantidadePessoas, DadosAnuncioResponse anuncio, Periodo periodo, Pagamento pagamento) {
        this.idReserva = idReserva;
        this.solicitante = solicitante;
        this.quantidadePessoas = quantidadePessoas;
        this.anuncio = anuncio;
        this.periodo = periodo;
        this.pagamento = pagamento;
    }
}
