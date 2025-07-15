package com.paranoia.ParanoiaAPI.dto.Usuario;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UsuarioLoginDTO {
    private String ip;
    private String pais;
    private String estado;
    private String cidade;
    private String email;
    private String senha;
}
