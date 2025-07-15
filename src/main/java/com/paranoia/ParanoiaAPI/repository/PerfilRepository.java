package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    @Query(nativeQuery = true, value = "SELECT RANKING.posicao FROM perfil AS P INNER JOIN" +
            " (SELECT *, @rownum \\:= @rownum + 1 AS posicao FROM perfil, (SELECT @rownum \\:= 0) r ORDER BY experiencia DESC) as RANKING" +
            " ON (P.id = RANKING.id) WHERE P.id = :perfilId")
    int posicaoRanking(@Param("perfilId") UUID perfilId);
}
