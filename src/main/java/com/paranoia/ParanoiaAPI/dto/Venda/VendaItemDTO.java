package com.paranoia.ParanoiaAPI.dto.Venda;

import com.paranoia.ParanoiaAPI.domain.Venda;
import com.paranoia.ParanoiaAPI.domain.VendaItem;
import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VendaItemDTO {
    private Integer quantidade;
    private BigDecimal valorIndividual;
    private BigDecimal valorTotal;
    private Produtos produto;

    public VendaItemDTO(VendaItem vendaItem) {
        this.quantidade = vendaItem.getQuantidade();
        this.valorIndividual = vendaItem.getValorIndividual();
        this.valorTotal = vendaItem.getValorTotal();
        this.produto = vendaItem.getProduto();
    }
}
