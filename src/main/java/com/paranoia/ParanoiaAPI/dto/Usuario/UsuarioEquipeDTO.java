package com.paranoia.ParanoiaAPI.dto.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioEquipeDTO {
    private String nome;
    private String hash;
    private String foto;
    private Boolean lider;

    public UsuarioEquipeDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.hash = usuario.getHash();
        this.foto = usuario.getPerfil().getFoto();
    }
}
