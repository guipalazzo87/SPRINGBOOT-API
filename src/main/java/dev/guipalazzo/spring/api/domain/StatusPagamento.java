package dev.guipalazzo.spring.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat
public enum StatusPagamento {

    PENDENTE,
    PAGO,
    ESTORNADO,
    CANCELADO
}
