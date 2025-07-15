package com.paranoia.ParanoiaAPI.helpers;

import com.paranoia.ParanoiaAPI.dto.Partida.DicaDTO;

import java.util.List;
import java.util.Set;

public interface ProdutoHelperInterface {
    List<DicaDTO> obterDicas(final List<DicaDTO> requisicaoDica,
                             final Set<Integer> indexDicasJaObtidas);
    List<String> obterVideos();
}
