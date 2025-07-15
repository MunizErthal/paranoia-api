package com.paranoia.ParanoiaAPI.dto.Perfil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paranoia.ParanoiaAPI.domain.enums.Niveis;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NivelDTO {
    int nivel;
    int expProximoNivel;
    int bitNoias;
    int proximoNivel;

    public NivelDTO(Niveis nivel) {
        this.nivel = nivel.getNivel();
        this.expProximoNivel = nivel.getExpProximoNivel();
        this.bitNoias = nivel.getBitNoias();
        this.proximoNivel = nivel.getProximoNivel().getNivel();
    }
}
