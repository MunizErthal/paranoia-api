package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Historico {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column
    private String descricao;
    @Column
    private LocalDateTime criadoEm;
    @Column
    @Enumerated(EnumType.STRING)
    private HistoricoAcoes codigoHistorico;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean erro;
}
