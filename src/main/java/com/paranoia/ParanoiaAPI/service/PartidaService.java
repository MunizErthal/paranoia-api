package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Partida;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import com.paranoia.ParanoiaAPI.dto.Partida.RankingPorProdutoDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.repository.EquipeRepository;
import com.paranoia.ParanoiaAPI.repository.PartidaRepository;
import com.paranoia.ParanoiaAPI.repository.UsuarioRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilService perfilService;
    private final HistoricoService historicoService;

    @Autowired
    public PartidaService(final PartidaRepository partidaRepository,
                          final UsuarioRepository usuarioRepository,
                          final HistoricoService historicoService,
                          final PerfilService perfilService) {
        this.partidaRepository = partidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoService = historicoService;
        this.perfilService = perfilService;
    }

    public Boolean verificarJogadores(final Equipe equipe,
                                      final Produtos produto) {
        return CollectionUtils.emptyIfNull(equipe.getUsuarios()).stream()
                .anyMatch(usuarioDaEquipe -> CollectionUtils.emptyIfNull(usuarioDaEquipe.getPartidas()).stream()
                        .anyMatch(partida -> partida.getProduto().equals(produto)));
    }

    public PartidaDTO iniciar(final Equipe equipe,
                              final Usuario lider,
                              final Produtos produto) {
        if (!produto.isJogoSimultaneo()) {
            partidaRepository.findByProdutoAndFinalizadoEm(produto, null)
                    .ifPresent(partida -> {
                        throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.INICIAR_PARTIDA_ERROR, lider, String.format("O jogo %s já está com uma partida em andamento.", produto.getNome()));
                    });
        }

        if (!equipe.getCriadoPor().getId().equals(lider.getId())) {
            throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.INICIAR_PARTIDA_ERROR, lider, "Apenas o líder da equipe pode iniciar partidas.");
        }

        var usuariosQueJaJogaram = CollectionUtils.emptyIfNull(equipe.getUsuarios()).stream()
                .filter(usuarioDaEquipe -> CollectionUtils.emptyIfNull(usuarioDaEquipe.getPartidas()).stream()
                        .anyMatch(partida -> partida.getProduto().equals(produto)))
                .toList();

        return Optional.of(partidaRepository.save(Partida.builder()
                        .equipe(equipe)
                        .produto(produto)
                        .numeroDeDicas(0)
                        .dicas(new ArrayList<>())
                        .usouVideo(Boolean.FALSE)
                        .resolvido(Boolean.FALSE)
                        .criadoEm(LocalDateTime.now())
                        .primeiraPartida(usuariosQueJaJogaram.isEmpty())
                        .numeroDeJogadores(equipe.getUsuarios().size())
                        .build()))
                .map(partida -> {
                    equipe.getUsuarios().forEach(usuario -> {
                        usuario.getPartidas().add(partida);
                        usuarioRepository.save(usuario);
                        perfilService.concederMedalhas(usuario, Set.of(Medalhas.JOGADOR, Medalhas.AGENTE_HOME_OFFICE));
                    });
                    return new PartidaDTO(partida);
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.INICIAR_PARTIDA_ERROR, lider, "Erro ao iniciar partida."));
    }

    public PartidaDTO finalizar(final Partida partida,
                                final Usuario usuario,
                                final String chute) {
        if (!List.of(partida.getProduto().getResposta(), partida.getProduto().getRespostaTempoEsgotado()).contains(chute)) {
            throw new ParanoiaException(HttpStatus.OK, HistoricoAcoes.FINALIZAR_PARTIDA_ERROR, usuario, "Resposta incorreta");
        }

        partida.setResolvido(Boolean.TRUE);
        partida.setFinalizadoEm(LocalDateTime.now());
        partida.setTempo(Duration.between(partida.getCriadoEm(), partida.getFinalizadoEm()));
        partida.setTempoEsgotado(partida.getProduto().getRespostaTempoEsgotado().equals(chute));
        partidaRepository.save(partida);

        partida.getEquipe().getUsuarios()
                .forEach(usuarioDaEquipe -> {
                    if (partida.getDicas().isEmpty()) {
                        perfilService.concederMedalha(usuario, Medalhas.MESTRE_HOME_OFFICE);
                    }
                    perfilService.concederMedalha(usuario, Medalhas.VENCEDOR_HOME_OFFICE);
                    usuarioRepository.save(usuarioDaEquipe);
                });

        return new PartidaDTO(partida);
    }

    public PartidaDTO finalizarAdministrador(Produtos produto) {
        var partida = partidaRepository.findByProdutoAndFinalizadoEm(produto, null)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.FINALIZAR_PARTIDA_ADMINISTRADOR_ERROR, null, "Partida não encontrada"));
        partida.setResolvido(Boolean.FALSE);
        partida.setFinalizadoEm(LocalDateTime.now());
        partida.setTempo(Duration.between(partida.getCriadoEm(), partida.getFinalizadoEm()));
        partidaRepository.save(partida);
        return new PartidaDTO(partida);
    }

    public Partida salvar(final Partida partida) {
        return this.partidaRepository.save(partida);
    }

    public List<RankingPorProdutoDTO> ranking() {
        return Stream.of(Produtos.values())
                .map(produto -> CollectionUtils.emptyIfNull(partidaRepository.findPartidaRankingByProduto(produto.name())).stream()
                        .map(PartidaDTO::new)
                        .collect(Collectors.toList()))
                .map(partidas -> {
                    if (partidas.isEmpty()) {
                        return null;
                    }

                    return new RankingPorProdutoDTO(partidas.get(0).getProduto(), partidas);
                })
                .collect(Collectors.toList());
    }

    public List<PartidaDTO> buscarRankingPorUsuario(final Usuario usuario) {
        return CollectionUtils.emptyIfNull(partidaRepository.findPartidaRankingByUsuario(usuario.getId()))
                .stream()
                .map(PartidaDTO::new)
                .peek(partida -> partida.setPosicaoRanking(partidaRepository.findPartidaPosicaoRanking(partida.getId(), partida.getProduto())))
                .collect(Collectors.toList());
    }
}
