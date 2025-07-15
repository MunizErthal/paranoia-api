package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.dto.Partida.DicaDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Partida {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column
    private LocalDateTime criadoEm;

    @Column
    private LocalDateTime finalizadoEm;

    @Column
    private int numeroDeDicas;

    @Column
    private int numeroDeJogadores;

    @Column
    private Duration tempo;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean primeiraPartida;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean resolvido;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean tempoEsgotado;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean usouVideo;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<DicaDTO> dicas;

    @Column
    @Enumerated(EnumType.STRING)
    private Produtos produto;

    @ManyToOne
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;

    @ManyToMany(mappedBy = "partidas")
    private List<Usuario> usuarios = new ArrayList<>();
}