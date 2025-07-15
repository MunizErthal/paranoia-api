package com.paranoia.ParanoiaAPI.domain.enums;

import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.helpers.AEntregaDeMakarovHelper;
import com.paranoia.ParanoiaAPI.helpers.ProdutoHelperInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Produtos {
    A_ENTREGA_DE_MAKAROV("A entrega de Makarov", "55c989c5a8b794990bf6a96180dce458", "0230", "0232", Boolean.FALSE,
            Boolean.FALSE, ProdutoTipo.ESCAPE_DE_MESA, new BigDecimal("500.00"), "dicas/aentregademakarov.json",
            null, AEntregaDeMakarovHelper.class);

    final String nome;
    final String hash;
    final String resposta;
    final String respostaTempoEsgotado;
    final boolean estoque;
    final boolean jogoSimultaneo;
    final ProdutoTipo tipo;
    final BigDecimal valor;
    final String arquivoDicas;
    final Integer quantidadeEmEstoque;
    private Class<? extends ProdutoHelperInterface> helper;

    public Class<? extends ProdutoHelperInterface> getHelper() {
        return helper;
    }

    public static Produtos getByHash(String hash) {
        return Stream.of(values())
                .filter(produto -> produto.getHash().equals(hash))
                .findFirst()
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.OBTER_CONVITES_EQUIPE_ERROR, hash));
    }
}