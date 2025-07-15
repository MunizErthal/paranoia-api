package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Usuario findByEmailAndSenha(final String email,
                                final String senha);
    Usuario findByEmail(final String email);
    Usuario findByCpf(String cpf);
    Usuario findByCodigoResetarSenha(String codigoResetarSenha);
    Usuario findByCodigoConfirmacaoEmail(String codigoConfirmacaoEmail);
    @Query(nativeQuery = true, value = "SELECT U.* FROM usuario AS U INNER JOIN usuarios_equipes AS UE ON (UE.usuario_id = U.id) WHERE UE.equipe_id = :equipeId")
    List<Usuario> findByEquipe(@Param("equipeId") UUID equipeId);
    @Query(nativeQuery = true, value = "SELECT U.* FROM usuario AS U INNER JOIN perfil AS P ON (P.id = U.perfil_id) WHERE U.email NOT IN('fernando.m.erthal@gmail.com', 'rafaelamaurer@gmail.com') ORDER BY experiencia DESC LIMIT :quantidade")
    List<Usuario> buscarRanking(@Param("quantidade") int quantidade);
}
