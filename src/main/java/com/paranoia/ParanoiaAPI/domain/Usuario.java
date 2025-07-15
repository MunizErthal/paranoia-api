package com.paranoia.ParanoiaAPI.domain;

import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioCriacaoDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.nonNull;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column
    private String nome;

    @Column
    private String hash;

    @Column
    private String sobrenome;

    @Column
    private String email;

    @Column
    private LocalDateTime criadoEm;

    @Column
    private String senha;

    @Column
    private String codigoResetarSenha;

    @Column
    private String codigoConfirmacaoEmail;

    @Column
    private String cpf;

    @Column
    private String telefone;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean emailConfirmado;

    @OneToOne
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @ManyToMany
    @JoinTable(name="usuarios_equipes",
            joinColumns=@JoinColumn(name="usuario_id"),
            inverseJoinColumns=@JoinColumn(name="equipe_id")
    )
    private List<Equipe> equipes = new ArrayList<>();

    @ManyToMany
    @JoinTable(name="usuarios_partidas",
            joinColumns=@JoinColumn(name="usuario_id"),
            inverseJoinColumns=@JoinColumn(name="partida_id")
    )
    private List<Partida> partidas = new ArrayList<>();

    public Usuario(UsuarioCriacaoDTO userDTO) {
        this.nome = userDTO.getNome();
        this.sobrenome = userDTO.getSobrenome();
        this.email = userDTO.getEmail();
        this.criadoEm = LocalDateTime.now();
        this.cpf = userDTO.getCpf();
        this.telefone = userDTO.getTelefone();
        this.emailConfirmado = nonNull(userDTO.getEmailConfirmado()) && userDTO.getEmailConfirmado();
    }
}