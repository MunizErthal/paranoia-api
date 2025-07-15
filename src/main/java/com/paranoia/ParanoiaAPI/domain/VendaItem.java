package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column
    private Integer quantidade;

    @Column
    private BigDecimal valorIndividual;

    @Column
    private BigDecimal valorTotal;

    @Column
    @Enumerated(EnumType.STRING)
    private Produtos produto;

    @ManyToOne
    private Venda venda;
}
