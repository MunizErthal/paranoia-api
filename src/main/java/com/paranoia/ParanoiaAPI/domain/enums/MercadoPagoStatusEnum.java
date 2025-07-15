package com.paranoia.ParanoiaAPI.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MercadoPagoStatusEnum {
    PENDING("pending"),
    APPROVED("approved"),
    AUTHORIZED("authorized"),
    IN_PROCESS("in_process"),
    IN_MEDIATION("in_mediation"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    REFUNDED("refunded"),
    CHARGED_BACK("charged_back");

    final String nome;
}
