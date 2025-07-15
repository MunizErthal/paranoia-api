package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.domain.enums.MercadoPagoStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venda {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column
    private LocalDateTime pagoEm;

    @Column
    private LocalDateTime compradoEm;

    @Column
    private LocalDateTime reservadoPara;

    @Column
    private BigDecimal valorTotal;

    @Column
    private Integer bitnoia;

    @ManyToOne
    private Usuario usuario;

    @Column
    private String cupom;

    @Column
    private Boolean entrega;

    @Column
    private BigDecimal frete;

    @Column
    @Enumerated(EnumType.STRING)
    private MercadoPagoStatusEnum status;

    @Column
    private String mercadoPagoId;

    @ManyToOne
    private Endereco endereco;
}