package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.dto.Equipe.ConviteEquipeDTO;
import com.paranoia.ParanoiaAPI.dto.Partida.DicaDTO;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.service.AutenticadorService;
import com.paranoia.ParanoiaAPI.service.DicaService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/dica")
@CrossOrigin(origins = "*")
public class DicaController {
    private final DicaService dicaService;
    private final AutenticadorService autenticadorService;
    @Autowired
    public DicaController(final DicaService dicaService,
                          final AutenticadorService autenticadorService) {
        this.dicaService = dicaService;
        this.autenticadorService = autenticadorService;
    }

    @PostMapping("/solicitar")
    public List<DicaDTO> solicitar(@RequestHeader(name = "token") String token,
                                   @RequestParam(value = "partidaId") UUID partidaId,
                                   @RequestBody List<DicaDTO> requisicaoDica) {
        var usuario = this.autenticadorService.autorizar(token);
        return Optional.ofNullable(usuario)
                .map(u -> CollectionUtils.emptyIfNull(u.getPartidas()).stream()
                        .filter(partida -> partida.getId().equals(partidaId))
                        .findFirst()
                        .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_PARTIDA_ERROR, usuario, "Partida não encontrada")))
                .map(partida -> dicaService.obterDicas(partida, requisicaoDica))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_CONVITES_EQUIPE_ERROR, usuario, "Erro ao solicitar dica"));
    }

    @GetMapping("/video")
    public List<String> video(@RequestHeader(name = "token") String token,
                              @RequestParam(value = "partidaId") UUID partidaId) {
        var usuario = this.autenticadorService.autorizar(token);
        return Optional.ofNullable(usuario)
                .map(u -> CollectionUtils.emptyIfNull(u.getPartidas()).stream()
                        .filter(partida -> partida.getId().equals(partidaId))
                        .findFirst()
                        .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_PARTIDA_ERROR, usuario, "Partida não encontrada")))
                .map(dicaService::obterVideos)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_CONVITES_EQUIPE_ERROR, usuario, "Erro ao solicitar dica"));
    }
}