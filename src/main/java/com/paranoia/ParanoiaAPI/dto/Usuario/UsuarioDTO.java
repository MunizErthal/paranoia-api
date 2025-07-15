package com.paranoia.ParanoiaAPI.dto.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import com.paranoia.ParanoiaAPI.dto.Perfil.PerfilDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioDTO {
    private String token;
    private String nome;
    private String hash;
    private String sobrenome;
    private String email;
    private LocalDateTime criadoEm;
    private String cpf;
    private String telefone;
    private Boolean emailConfirmado;
    private PerfilDTO perfil;
    private List<PartidaDTO> partidasEmAndamento;

    public UsuarioDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.hash = usuario.getHash();
        this.sobrenome = usuario.getSobrenome();
        this.email = usuario.getEmail();
        this.criadoEm = usuario.getCriadoEm();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.emailConfirmado = usuario.getEmailConfirmado();
        this.perfil = new PerfilDTO(usuario.getPerfil());
    }
}
