package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, UUID> {
    Optional<Equipe> findByNomeAndAtiva(String nome, Boolean ativa);
}
