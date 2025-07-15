package com.paranoia.ParanoiaAPI.dto.Venda;

import com.paranoia.ParanoiaAPI.domain.Endereco;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnderecoDTO {
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;

    EnderecoDTO(Endereco endereco) {
        this.cidade = endereco.getCidade();
        this.bairro = endereco.getBairro();
        this.rua = endereco.getRua();
        this.numero = endereco.getNumero();
        this.complemento = endereco.getComplemento();
    }
}
