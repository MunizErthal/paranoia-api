package com.paranoia.ParanoiaAPI.dto.Partida;

import com.paranoia.ParanoiaAPI.domain.Partida;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
public class PartidaDTO {
    private UUID id;
    private Integer posicaoRanking;
    private String tempoDeJogo;
    private String produto;
    private String produtoCodigo;
    private Boolean resolvido;
    private Boolean usouVideo;
    private String nomeEquipe;
    private int numeroDeDicas;
    private List<DicaDTO> dicas;
    private int numeroDeJogadores;
    private LocalDateTime criadoEm;
    private Boolean primeiraPartida;
    private LocalDateTime finalizadoEm;
    private List<UsuarioDTO> usuarios;

    public PartidaDTO(Partida partida) {
        this.id = partida.getId();
        this.dicas = partida.getDicas();
        this.criadoEm = partida.getCriadoEm();
        this.resolvido = partida.getResolvido();
        this.usouVideo = partida.getUsouVideo();
        this.finalizadoEm = partida.getFinalizadoEm();
        this.produtoCodigo = partida.getProduto().name();
        this.produto = partida.getProduto().getNome();
        this.numeroDeDicas = partida.getNumeroDeDicas();
        this.nomeEquipe = partida.getEquipe().getNome();
        this.primeiraPartida = partida.getPrimeiraPartida();
        this.numeroDeJogadores = partida.getNumeroDeJogadores();

        if (isNull(this.finalizadoEm)) {
            this.tempoDeJogo = formatarDuracao(Duration.between(partida.getCriadoEm(), LocalDateTime.now()));
        } else {
            this.tempoDeJogo = formatarDuracao(partida.getTempo());
        }

        this.usuarios = CollectionUtils.emptyIfNull(partida.getUsuarios()).stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }

    private String formatarDuracao(Duration duration) {
        return LocalTime.MIDNIGHT.plus(duration)
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
