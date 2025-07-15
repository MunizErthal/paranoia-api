package com.paranoia.ParanoiaAPI.dto.Usuario;

import com.paranoia.ParanoiaAPI.dto.Perfil.PerfilDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioCriacaoDTO {
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private Boolean emailConfirmado;
    private String indicadoPor;
    private UUID equipeId;

    private PerfilDTO perfilCriacaoJSON;
    private String criadoEmCriacaoJSON;
    private List<Integer> medalhasIdsCriacaoJSON;
}
