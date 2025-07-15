package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Partida;
import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, UUID> {
    Optional<Partida> findPartidaByEquipeAndFinalizadoEm(Equipe equipeParaDeletar,
                                                         LocalDateTime finalizadoEm);

    Optional<Partida> findByProdutoAndFinalizadoEm(Produtos produto,
                                                   LocalDateTime finalizadoEm);

    @Query(nativeQuery = true, value = "SELECT P.* FROM partida AS P INNER JOIN equipe AS E ON E.id = P.equipe_id WHERE usou_video IS FALSE AND produto = :produto AND P.finalizado_em IS NOT NULL ORDER BY tempo ASC")
    List<Partida> findPartidaRankingByProduto(@Param("produto") String produto);

    @Query(nativeQuery = true, value = "SELECT P.* FROM usuarios_partidas AS UP INNER JOIN partida AS P ON P.id = UP.partida_id WHERE UP.usuario_id = :usuarioId AND P.finalizado_em IS NOT NULL ORDER BY tempo ASC")
    List<Partida> findPartidaRankingByUsuario(@Param("usuarioId") UUID usuarioId);

    @Query(nativeQuery = true, value = "SELECT RANKING.posicao FROM partida AS P INNER JOIN" +
            " (SELECT *, @rownum \\:= @rownum + 1 AS posicao FROM partida, (SELECT @rownum \\:= 0) r WHERE usou_video IS FALSE AND produto = :produto AND partida.finalizado_em IS NOT NULL ORDER BY tempo DESC) as RANKING" +
            " ON (P.id = RANKING.id) WHERE P.id = :partidaId AND P.finalizado_em IS NOT NULL")
    Integer findPartidaPosicaoRanking(@Param("partidaId") UUID partidaId, @Param("produto") String produto);
}
