package dev.guipalazzo.spring.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat
public enum FormaPagamento {

    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    DINHEIRO

}
