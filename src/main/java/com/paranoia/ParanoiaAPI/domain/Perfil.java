package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.domain.enums.Niveis;
import com.paranoia.ParanoiaAPI.dto.Perfil.PerfilDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Perfil {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column
    private int experiencia;
    @Column
    private int bitnoias;
    @Column
    private String foto;
    @Column
    @Enumerated(EnumType.STRING)
    private Niveis nivel;
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Medalhas> medalhas;

    public Perfil(PerfilDTO perfilDTO) {
        this.experiencia = perfilDTO.getExperiencia();
        this.bitnoias = perfilDTO.getBitnoias();
        this.foto = perfilDTO.getFoto();
        this.nivel = Niveis.valueOf(perfilDTO.getNivelNameCriacaoJSON().toUpperCase());
    }
}