package com.paranoia.ParanoiaAPI.dto.Perfil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedalhaDTO {
    String codigo;
    String nome;
    String descricao;
    Boolean mostrarDescricao;
    Boolean temMedalha;
}
