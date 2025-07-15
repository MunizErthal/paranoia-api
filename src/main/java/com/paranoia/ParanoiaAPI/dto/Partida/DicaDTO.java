package com.paranoia.ParanoiaAPI.dto.Partida;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DicaDTO {
    private String dica;
    private Integer index;
    private Boolean selecionada = Boolean.FALSE;
    private Boolean dicaFinal = Boolean.FALSE;
    private List<Integer> indexPai;

    public DicaDTO(String dica, Boolean selecionada) {
        this.dica = dica;
        this.selecionada = selecionada;
    }

    public DicaDTO(int index, String dica) {
        this.index = index;
        this.dica = dica;
    }

    public void setSelecionada(Boolean selecionada) {
        Optional.ofNullable(selecionada)
                .ifPresentOrElse(aBoolean -> this.selecionada = aBoolean,
                        () -> this.selecionada = Boolean.FALSE);
    }

    public void setDicaFinal(Boolean dicaFinal) {
        Optional.ofNullable(dicaFinal)
                .ifPresentOrElse(aBoolean -> this.dicaFinal = aBoolean,
                        () -> this.dicaFinal = Boolean.FALSE);
    }
}
