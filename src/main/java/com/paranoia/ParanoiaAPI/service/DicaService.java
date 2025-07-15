package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Partida;
import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.dto.Partida.DicaDTO;
import com.paranoia.ParanoiaAPI.factory.ProdutoHelperFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DicaService {
    private final HistoricoService historicoService;
    private final PartidaService partidaService;
    private final PerfilService perfilService;
    private final ProdutoHelperFactory produtoHelperFactory;

    @Autowired
    public DicaService(final ProdutoHelperFactory produtoHelperFactory,
                       final HistoricoService historicoService,
                       final PartidaService partidaService,
                       final PerfilService perfilService) {
        this.produtoHelperFactory = produtoHelperFactory;
        this.historicoService = historicoService;
        this.partidaService = partidaService;
        this.perfilService = perfilService;
    }

    public List<DicaDTO> obterDicas(Partida partida, List<DicaDTO> requisicaoDica) {
        var indexDicasJaObtidas = CollectionUtils.emptyIfNull(partida.getDicas()).stream()
                .map(DicaDTO::getIndex)
                .collect(Collectors.toSet());

        var dica = produtoHelperFactory.obterHelper(partida.getProduto())
                .obterDicas(requisicaoDica, indexDicasJaObtidas);

        if (dica.size() == 1 && dica.get(0).getDicaFinal()) {
            partida.setNumeroDeDicas(partida.getNumeroDeDicas() + 1);
            partida.getDicas().addAll(dica);
            perfilService.concederMedalhaParaEquipe(partida.getEquipe(), Medalhas.A_INTELIGENCIA_SERVE_PRA_ISSO);
        }

        return dica;
    }

    public List<String> obterVideos(Partida partida) {
        partida.setUsouVideo(Boolean.TRUE);
        partidaService.salvar(partida);
        return produtoHelperFactory.obterHelper(partida.getProduto()).obterVideos();
    }
}
