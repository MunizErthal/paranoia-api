package com.paranoia.ParanoiaAPI.dto.Venda;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VendaCriacaoDTO {
    private LocalDateTime reservadoPara;
    private BigDecimal valorTotal;
    private BigDecimal frete;
    private int bitnoia;
    private String cupom;
    private String mercadoPagoId;
    private boolean retirada;
    private List<VendaCriacaoItensDTO> itens;
    private EnderecoDTO endereco;
}
