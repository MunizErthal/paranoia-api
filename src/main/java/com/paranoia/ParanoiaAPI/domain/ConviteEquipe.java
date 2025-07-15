package com.paranoia.ParanoiaAPI.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConviteEquipe {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;
    @ManyToOne
    @JoinColumn(name = "enviado_por", nullable = false)
    private Usuario enviadoPor;
    @ManyToOne
    @JoinColumn(name = "enviado_para", nullable = false)
    private Usuario enviadoPara;
}