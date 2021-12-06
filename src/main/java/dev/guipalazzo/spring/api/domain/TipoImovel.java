package dev.guipalazzo.spring.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum TipoImovel {

    APARTAMENTO("Apartamento"),
    CASA("Casa"),
    HOTEL("Hotel"),
    POUSADA("Pousada");

    private String descricao;


}
