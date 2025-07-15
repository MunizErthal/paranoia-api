package com.paranoia.ParanoiaAPI.helpers;

import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.dto.Partida.DicaDTO;
import com.paranoia.ParanoiaAPI.utils.JSONUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.paranoia.ParanoiaAPI.domain.enums.Produtos.A_ENTREGA_DE_MAKAROV;

@Component
public class AEntregaDeMakarovHelper implements ProdutoHelperInterface {

    private static final String MEDO = "MEDO";
    private static final String ENGRENAGEM = "ENGRENAGEM";
    private static final String PARABENS = "PARABENS";
    private static final String INVERTIDA = "INVERTIDA";
    private static final String BOMBA = "BOMBA";
    private static final Set<String> TODAS_AS_CAIXAS = Set.of(MEDO, ENGRENAGEM, PARABENS, INVERTIDA, BOMBA);

    @Override
    public List<DicaDTO> obterDicas(final List<DicaDTO> requisicaoDica,
                                    final Set<Integer> indexDicasJaObtidas) {
        if (CollectionUtils.isEmpty(requisicaoDica)) {
            return obterCaixas();
        } else if (TODAS_AS_CAIXAS.containsAll(requisicaoDica.stream().map(DicaDTO::getDica).toList())) {
            return obterQuestoes(requisicaoDica);
        } else {
            return obterDica(requisicaoDica.get(0), indexDicasJaObtidas);
        }
    }

    public static List<DicaDTO> obterCaixas() {
        return List.of(new DicaDTO(MEDO, Boolean.FALSE),
                new DicaDTO(ENGRENAGEM, Boolean.FALSE),
                new DicaDTO(PARABENS, Boolean.FALSE),
                new DicaDTO(INVERTIDA, Boolean.FALSE),
                new DicaDTO(BOMBA, Boolean.FALSE));
    }

    public static List<DicaDTO> obterQuestoes(final List<DicaDTO> caixas) {
        var selecionadas = CollectionUtils.emptyIfNull(caixas).stream()
                .filter(DicaDTO::getSelecionada)
                .map(DicaDTO::getDica)
                .toList();

        if (selecionadas.isEmpty() || List.of(PARABENS).containsAll(selecionadas)) {
            return List.of(new DicaDTO(1, "NÃO SEI O QUE PRECISA SER FEITO"));
        } else if (List.of(MEDO).containsAll(selecionadas)) {
            return List.of(new DicaDTO(2, "NÃO SEI O QUE PRECISA SER FEITO"),
                    new DicaDTO(3, "JÁ FIZ O QUE PRECISA SER FEITO, MAS NÃO SEI COMO USAR O QUE ENCONTREI"));
        } else if (List.of(MEDO, ENGRENAGEM).containsAll(selecionadas)) {
            return List.of(new DicaDTO(4, "NÃO SEI O QUE PRECISA SER FEITO APÓS ABRIR ESTAS CAIXAS"),
                    new DicaDTO(5, "JÁ ABRI TODAS AS CAIXAS QUE ESTAVAM DENTRO"));
        } else if (List.of(MEDO, PARABENS).containsAll(selecionadas)) {
            return List.of(new DicaDTO(6, "NÃO SEI O QUE PRECISA SER FEITO"),
                    new DicaDTO(7, "JÁ FIZ O QUE PRECISA SER FEITO COM OS ALBUNS, MAS NÃO SEI COMO USAR O QUE ENCONTREI"));
        } else if (List.of(MEDO, PARABENS, ENGRENAGEM).containsAll(selecionadas)) {
            return List.of(new DicaDTO(8, "NÃO ABRI A LATA"),
                    new DicaDTO(9, "NÃO ABRI A CAIXA COM CADEADO DE CHAVE"),
                    new DicaDTO(10, "PRECISO DE DICA SOBRE O QUE VEM APARTIR DA CAIXA COM CADEADO"),
                    new DicaDTO(11, "JA ABRI A LATA MAS NÃO SEI O QUE FAZER COM O QUE ESTAVA DENTRO"));
        } else if (List.of(MEDO, PARABENS, ENGRENAGEM, INVERTIDA).containsAll(selecionadas)) {
            return List.of(new DicaDTO(12, "NÃO SEI O QUE PRECISA SER FEITO"),
                    new DicaDTO(13, "JÁ DESCOBRI O QUE A MENSAGEM ESTAVA ESCONDENDO"),
                    new DicaDTO(14, "DESCOBRI A SENHA PARA ACESSAR OS ARQUIVOS"),
                    new DicaDTO(15, "NÃO SEI COMO USAR ZIP"),
                    new DicaDTO(16, "NÃO SEI COMO BAIXAR APLICATIVOS"));
        } else if (selecionadas.size() == TODAS_AS_CAIXAS.size()) {
            return List.of(new DicaDTO(17, "NÃO SEI POR ONDE COMEÇAR"),
                    new DicaDTO(18, "JÁ ABRI UMA DAS CAIXAS"),
                    new DicaDTO(19, "JÁ ABRIMOS AS DUAS CAIXAS"));
        } else {
            return List.of(new DicaDTO(666, "AS CAIXAS SELECIONADAS NÃO ABREM SEM OUTRAS CAIXAS JÁ TEREM SIDO ABERTAS, VOCÊ TEM CERTEZA QUE SELECIONOU AS CAIXAS CORRETAS?"));
        }
    }

    public static List<DicaDTO> obterDica(final DicaDTO questao,
                                          final Set<Integer> indexDicasJaObtidas) {
        var dicas = CollectionUtils.emptyIfNull(JSONUtils.lerArquivoDicasJSON(A_ENTREGA_DE_MAKAROV.getArquivoDicas(), DicaDTO.class));
        return dicas.stream().filter(dica -> dica.getIndexPai().contains(questao.getIndex())
                        && !indexDicasJaObtidas.contains(dica.getIndex()))
                .min(Comparator.comparing(DicaDTO::getIndex))
                .stream()
                .peek(dicaDTO -> dicaDTO.setDicaFinal(Boolean.TRUE))
                .toList();
    }

    @Override
    public List<String> obterVideos() {
        return List.of("Etapa 1", "Etapa 2", "Etapa 3", "Etapa 4", "Etapa 5", "Etapa 6", "Etapa 7");
    }
}