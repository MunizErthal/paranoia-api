package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.UsuarioToken;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticadorService {
    private final HistoricoService historicoService;

    private final UsuarioTokenService usuarioTokenService;

    @Autowired
    AutenticadorService(final HistoricoService historicoService,
                        final UsuarioTokenService usuarioTokenService) {
        this.historicoService = historicoService;
        this.usuarioTokenService = usuarioTokenService;
    }

    public Usuario autorizar(final String token) {
        return Optional.ofNullable(usuarioTokenService.validarToken(token))
                .map(UsuarioToken::getUsuario)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, HistoricoAcoes.TOKEN_EXPIRADO_ERROR, null, "Sessão expirada."));
    }
}