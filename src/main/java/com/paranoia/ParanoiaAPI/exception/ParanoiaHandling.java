package com.paranoia.ParanoiaAPI.exception;

import com.paranoia.ParanoiaAPI.dto.Retornos.MensagemRetornoDTO;
import com.paranoia.ParanoiaAPI.service.HistoricoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class ParanoiaHandling {
    private final HistoricoService historicoService;

    @Autowired
    ParanoiaHandling(final HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @ExceptionHandler(ParanoiaException.class)
    public ResponseEntity<MensagemRetornoDTO> handle(ParanoiaException e) {
        this.historicoService.registrar(e.getUsuario(), e.getAcao(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(buildExceptionReturn(e));
    }

    private MensagemRetornoDTO buildExceptionReturn(ParanoiaException e) {
        var retorno = new MensagemRetornoDTO();
        retorno.setMensagem(e.getMessage());
        retorno.setStatus(String.valueOf(e.getStatusCode().value()));
        return retorno;
    }
}
