package com.paranoia.ParanoiaAPI.dto.Venda;


import com.paranoia.ParanoiaAPI.domain.Venda;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VendaDTO {
    private UUID id;
    private LocalDateTime pagoEm;
    private LocalDateTime compradoEm;
    private LocalDateTime reservadoPara;
    private BigDecimal valorTotal;
    private BigDecimal frete;
    private Integer bitnoia;
    private UsuarioDTO usuario;
    private String cupom;
    private String mercadoPagoId;
    private boolean retirada;
    private List<VendaItemDTO> itens;
    private EnderecoDTO endereco;

    public VendaDTO(Venda venda) {
        this.id = venda.getId();
        this.pagoEm = venda.getPagoEm();
        this.compradoEm = venda.getCompradoEm();
        this.reservadoPara = venda.getReservadoPara();
        this.valorTotal = venda.getValorTotal();
        this.mercadoPagoId = venda.getMercadoPagoId();
        this.bitnoia = venda.getBitnoia();
        this.usuario = new UsuarioDTO(venda.getUsuario());
        this.cupom = venda.getCupom();
        this.endereco = new EnderecoDTO(venda.getEndereco());
    }
}
