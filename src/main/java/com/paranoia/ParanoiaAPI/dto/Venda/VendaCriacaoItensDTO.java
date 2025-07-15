package com.paranoia.ParanoiaAPI.dto.Venda;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VendaCriacaoItensDTO {
    private int quantidade;
    private String produto;
    private BigDecimal valorIndividual;
    private BigDecimal valorTotal;
}
