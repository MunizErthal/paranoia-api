package com.paranoia.ParanoiaAPI.exception;

import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ParanoiaException extends RuntimeException {
    public HttpStatus statusCode;
    public HistoricoAcoes acao;
    public Usuario usuario;

    public ParanoiaException(final HttpStatus statusCode,
                             final HistoricoAcoes acao,
                             final Usuario usuario,
                             final String mensagem,
                             final Object ... args) {
        super(String.format(mensagem, args));
        this.acao = acao;
        this.usuario = usuario;
        this.statusCode = statusCode;
    }

    public ParanoiaException(final HttpStatus statusCode,
                             final HistoricoAcoes acao,
                             final Usuario usuario,
                             final Object ... args) {
        super(String.format(acao.getMensagem(), args));
        this.acao = acao;
        this.usuario = usuario;
        this.statusCode = statusCode;
    }

    public ParanoiaException(final HttpStatus statusCode,
                             final HistoricoAcoes acao,
                             final Object ... args) {
        super(String.format(acao.getMensagem(), args));
        this.acao = acao;
        this.statusCode = statusCode;
    }

    public ParanoiaException(final HttpStatus statusCode,
                             final HistoricoAcoes acao) {
        super(acao.getMensagem());
        this.acao = acao;
        this.statusCode = statusCode;
    }
}
