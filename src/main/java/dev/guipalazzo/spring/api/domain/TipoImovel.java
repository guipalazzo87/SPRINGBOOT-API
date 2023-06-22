package dev.guipalazzo.spring.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoImovel {

    APARTAMENTO("Apartamento"),
    CASA("Casa"),
    HOTEL("Hotel"),
    POUSADA("Pousada");

    private final String descricao;


}
