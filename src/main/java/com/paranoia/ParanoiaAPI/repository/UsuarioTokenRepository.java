package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.UsuarioToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioTokenRepository extends JpaRepository<UsuarioToken, UUID> {
    UsuarioToken findByToken(String token);
}
