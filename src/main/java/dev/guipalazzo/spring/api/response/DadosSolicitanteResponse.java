package dev.guipalazzo.spring.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class DadosSolicitanteResponse {
    @JsonFormat
    private Long id;
    @JsonFormat
    private String nome;

    public DadosSolicitanteResponse(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
