package com.paranoia.ParanoiaAPI.dto.Partida;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RankingPorProdutoDTO {
    public String produto;
    public List<PartidaDTO> partidas;

    public RankingPorProdutoDTO(String produto,
                                List<PartidaDTO> partidas) {
        this.produto = produto;
        this.partidas = partidas;
    }
}
