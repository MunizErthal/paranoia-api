package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.UsuarioToken;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioLoginDTO;
import com.paranoia.ParanoiaAPI.repository.UsuarioTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioTokenService {
    private final UsuarioTokenRepository usuarioTokenRepository;

    @Autowired
    UsuarioTokenService(final UsuarioTokenRepository usuarioTokenRepository) {
        this.usuarioTokenRepository = usuarioTokenRepository;
    }

    public UsuarioToken validarToken(final String token) {
        return this.usuarioTokenRepository.findByToken(token);
    }

    public void delete(UsuarioToken usuarioToken) {
        this.usuarioTokenRepository.delete(usuarioToken);
    }

    public UsuarioToken registrarToken(final UsuarioLoginDTO usuarioLogin,
                                       final Usuario usuario,
                                       final String token) {
        return this.usuarioTokenRepository.save(UsuarioToken.builder()
                .cidade(usuarioLogin.getCidade())
                .estado(usuarioLogin.getEstado())
                .criadoEm(LocalDateTime.now())
                .pais(usuarioLogin.getPais())
                .ip(usuarioLogin.getIp())
                .usuario(usuario)
                .token(token)
                .build());
    }
}
