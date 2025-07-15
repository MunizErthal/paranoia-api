package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import com.paranoia.ParanoiaAPI.dto.Partida.RankingPorProdutoDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.service.AutenticadorService;
import com.paranoia.ParanoiaAPI.service.PartidaService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/partida")
@CrossOrigin(origins = "*")
public class PartidaController {

    private final static String SEM_EQUIPE = "Jogar Sem Equipe";
    private final PartidaService partidaService;
    private final AutenticadorService autenticadorService;
    private final Set<String> adminEmails;

    @Autowired
    public PartidaController(@Value("${admin.emails}") Set<String> adminEmails,
                             final PartidaService partidaService,
                             final AutenticadorService autenticadorService) {
        this.adminEmails = adminEmails;
        this.partidaService = partidaService;
        this.autenticadorService = autenticadorService;
    }

    @PostMapping("/iniciar")
    public PartidaDTO iniciar(@RequestHeader(name = "token") String token,
                              @RequestParam(value = "nomeEquipe") String nomeEquipe,
                              @RequestParam(value = "produtoHash") String produtoHash) {
        var usuario = this.autenticadorService.autorizar(token);
        return CollectionUtils.emptyIfNull(usuario.getEquipes()).stream()
                .filter(equipe -> equipe.getAtiva()
                        && equipe.getNome().equals(nomeEquipe) || (SEM_EQUIPE.equals(nomeEquipe) && equipe.getEquipeSolo()))
                .findFirst()
                .map(equipe -> partidaService.iniciar(equipe, usuario, Produtos.getByHash(produtoHash)))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.INICIAR_PARTIDA_ERROR, usuario, "Equipe não encontrada"));
    }

    @PutMapping("/finalizar_admin")
    public PartidaDTO finalizarAdministrador(@RequestHeader(name = "token") String token,
                                             @RequestParam(value = "produtoHash") String produtoHash) {
        return Optional.ofNullable(this.autenticadorService.autorizar(token))
                .filter(admin -> adminEmails.contains(admin.getEmail()))
                .map(usuario -> partidaService.finalizarAdministrador(Produtos.getByHash(produtoHash)))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.FINALIZAR_PARTIDA_ERROR));
    }

    @PutMapping("/finalizar")
    public PartidaDTO finalizar(@RequestHeader(name = "token") String token,
                                @RequestParam(value = "partidaId") UUID partidaId,
                                @RequestParam(value = "chute") String chute) {
        var usuario = this.autenticadorService.autorizar(token);
        return CollectionUtils.emptyIfNull(usuario.getPartidas()).stream()
                .filter(partida -> partida.getId().equals(partidaId))
                .findFirst()
                .map(partida -> partidaService.finalizar(partida, usuario, chute))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_PARTIDA_ERROR, usuario, "Partida não encontrada"));
    }

    @GetMapping("/verificarJogadores")
    public Boolean verificarJogadores(@RequestHeader(name = "token") String token,
                                      @RequestParam(value = "nomeEquipe") String nomeEquipe,
                                      @RequestParam(value = "produtoHash") String produtoHash) {
        var usuario = this.autenticadorService.autorizar(token);
        return CollectionUtils.emptyIfNull(usuario.getEquipes()).stream()
                .filter(equipe -> equipe.getAtiva()
                        && equipe.getNome().equals(nomeEquipe) || (SEM_EQUIPE.equals(nomeEquipe) && equipe.getEquipeSolo()))
                .findFirst()
                .map(equipe -> partidaService.verificarJogadores(equipe, Produtos.getByHash(produtoHash)))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.INICIAR_PARTIDA_ERROR, usuario, "Equipe não encontrada"));
    }

    @GetMapping
    public PartidaDTO obter(@RequestHeader(name = "token") String token,
                            @RequestParam(value = "partidaId") UUID partidaId) {
        var usuario = this.autenticadorService.autorizar(token);
        return CollectionUtils.emptyIfNull(usuario.getPartidas()).stream()
                .filter(partida -> partida.getId().equals(partidaId))
                .findFirst()
                .map(PartidaDTO::new)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_PARTIDA_ERROR, usuario, "Partida não encontrada"));
    }

    @GetMapping("/em_andamento_por_usuario")
    public List<PartidaDTO> obterEmAndamentoPorUsuario(@RequestHeader(name = "token") String token) {
        var usuario = this.autenticadorService.autorizar(token);
        return CollectionUtils.emptyIfNull(usuario.getPartidas()).stream()
                .filter(partida -> isNull(partida.getFinalizadoEm()))
                .map(PartidaDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/ranking")
    public List<RankingPorProdutoDTO> ranking() {
        return partidaService.ranking();
    }

    @GetMapping("/ranking_por_usuario")
    public List<PartidaDTO> ranking(@RequestHeader(name = "token") String token) {
        return Optional.ofNullable(this.autenticadorService.autorizar(token))
                .map(partidaService::buscarRankingPorUsuario)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.RANKING_PARTIDA_USUARIO_ERROR, null, "Ranking não pode ser encontrado"));
    }
}