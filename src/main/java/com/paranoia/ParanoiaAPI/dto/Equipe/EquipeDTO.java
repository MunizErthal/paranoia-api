package com.paranoia.ParanoiaAPI.dto.Equipe;

import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioEquipeDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class EquipeDTO {
    private UUID id;
    private String nome;
    private List<UsuarioEquipeDTO> usuarios;
    private Boolean lider;
}
