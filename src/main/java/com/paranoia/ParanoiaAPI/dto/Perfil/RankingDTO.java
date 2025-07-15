package com.paranoia.ParanoiaAPI.dto.Perfil;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RankingDTO {
    private String nome;
    private String sobrenome;
    private String foto;
    private int experiencia;
    private int nivel;
}
