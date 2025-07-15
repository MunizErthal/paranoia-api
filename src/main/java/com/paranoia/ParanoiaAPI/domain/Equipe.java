package com.paranoia.ParanoiaAPI.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Equipe {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column
    private String nome;
    @ManyToOne
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;
    @Column
    private LocalDateTime criadoEm;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean ativa;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean equipeSolo;
    @ManyToMany(mappedBy = "equipes")
    private List<Usuario> usuarios = new ArrayList<>();
}