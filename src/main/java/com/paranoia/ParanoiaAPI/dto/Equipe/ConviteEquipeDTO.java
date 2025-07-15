package com.paranoia.ParanoiaAPI.dto.Equipe;

import com.paranoia.ParanoiaAPI.domain.ConviteEquipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ConviteEquipeDTO {
    private UUID id;
    private String nomeEquipe;
    private String enviadoPor;

    public ConviteEquipeDTO(ConviteEquipe conviteEquipe) {
        this.id = conviteEquipe.getId();
        this.nomeEquipe = conviteEquipe.getEquipe().getNome();
        this.enviadoPor = conviteEquipe.getEnviadoPor().getNome();
    }
}
