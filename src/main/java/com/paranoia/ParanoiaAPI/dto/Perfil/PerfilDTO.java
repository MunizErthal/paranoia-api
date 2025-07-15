package com.paranoia.ParanoiaAPI.dto.Perfil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paranoia.ParanoiaAPI.domain.Perfil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerfilDTO {
    private int experiencia;
    private int bitnoias;
    private String foto;
    private int porcentagemProximoNivel;
    private int posicaoRanking;
    private NivelDTO nivel;
    private List<MedalhaDTO> medalhas;

    private String nivelNameCriacaoJSON;

    public PerfilDTO(Perfil perfil) {
        this.experiencia = perfil.getExperiencia();
        this.bitnoias = perfil.getBitnoias();
        this.foto = perfil.getFoto();
        this.nivel = new NivelDTO(perfil.getNivel());
    }
}
