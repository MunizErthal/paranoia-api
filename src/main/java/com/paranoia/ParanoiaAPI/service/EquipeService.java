package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.ConviteEquipe;
import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.dto.Equipe.ConviteEquipeDTO;
import com.paranoia.ParanoiaAPI.dto.Equipe.EquipeDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioEquipeDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.repository.ConviteEquipeRepository;
import com.paranoia.ParanoiaAPI.repository.EquipeRepository;
import com.paranoia.ParanoiaAPI.repository.PartidaRepository;
import com.paranoia.ParanoiaAPI.repository.UsuarioRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EquipeService {
    private final EquipeRepository equipeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConviteEquipeRepository conviteEquipeRepository;
    private final PartidaRepository partidaRepository;
    private final HistoricoService historicoService;
    private final MailService mailService;

    @Autowired
    public EquipeService(final EquipeRepository equipeRepository,
                         final UsuarioRepository usuarioRepository,
                         final ConviteEquipeRepository conviteEquipeRepository,
                         final PartidaRepository partidaRepository,
                         final HistoricoService historicoService,
                         final MailService mailService) {
        this.mailService = mailService;
        this.equipeRepository = equipeRepository;
        this.historicoService = historicoService;
        this.partidaRepository = partidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.conviteEquipeRepository = conviteEquipeRepository;
    }

    public EquipeDTO criar(final Usuario usuario,
                           final String nome) {
        equipeRepository.findByNomeAndAtiva(nome, Boolean.TRUE).ifPresent(equipe -> {
            throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CRIAR_EQUIPE_ERROR, usuario, "Já existe uma equipe com o nome %s.", nome);
        });

        var novaEquipe = Equipe.builder()
                .nome(nome)
                .criadoPor(usuario)
                .ativa(Boolean.TRUE)
                .equipeSolo(Boolean.FALSE)
                .usuarios(new ArrayList<>())
                .criadoEm(LocalDateTime.now())
                .build();
        novaEquipe.getUsuarios().add(usuario);

        return Optional.of(this.equipeRepository.save(novaEquipe))
                .map(equipe -> {
                    usuario.getEquipes().add(equipe);
                    usuarioRepository.save(usuario);
                    return mapearEquipe(equipe, usuario);
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.CRIAR_EQUIPE_ERROR, usuario, "Erro ao criar equipe."));
    }

    public EquipeDTO editar(final Usuario usuario,
                            final UUID equipeId,
                            final String nome) {
        var equipeParaEditar = CollectionUtils.emptyIfNull(usuario.getEquipes()).stream()
                .filter(equipe -> equipe.getAtiva()
                        && equipe.getId().equals(equipeId)
                        && equipe.getCriadoPor().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.DELETAR_EQUIPE_ERROR, usuario, "Equipe não pode ser encontrada entre as equipes do usuário"));

        equipeRepository.findByNomeAndAtiva(nome, Boolean.TRUE).ifPresent(equipe -> {
            throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CRIAR_EQUIPE_ERROR, usuario, "Já existe uma equipe com o nome %s.", nome);
        });

        equipeParaEditar.setNome(nome);
        equipeRepository.save(equipeParaEditar);
        return mapearEquipe(equipeParaEditar, usuario);
    }

    public Boolean deletar(final Usuario usuario,
                           final UUID equipeId) {
        var equipeParaDeletar = CollectionUtils.emptyIfNull(usuario.getEquipes()).stream()
                .filter(equipe -> equipe.getAtiva() && equipe.getId().equals(equipeId))
                .findFirst()
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.DELETAR_EQUIPE_ERROR, usuario, "Equipe não pode ser encontrada entre as equipes do usuário"));

        partidaRepository.findPartidaByEquipeAndFinalizadoEm(equipeParaDeletar, null).ifPresent(partida -> {
            throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.DELETAR_EQUIPE_ERROR, usuario, "Você não pode deletar uma equipe no meio de uma partida", equipeParaDeletar.getNome());
        });

        if (equipeParaDeletar.getCriadoPor().getId().equals(usuario.getId())) {
            equipeParaDeletar.setAtiva(Boolean.FALSE);
            equipeRepository.save(equipeParaDeletar);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<ConviteEquipeDTO> obterConvites(final Usuario usuario) {
        return CollectionUtils.emptyIfNull(conviteEquipeRepository.findByEnviadoPara(usuario)).stream()
                .map(ConviteEquipeDTO::new)
                .collect(Collectors.toList());
    }

    public EquipeDTO responderConvite(final Usuario usuario,
                                      final UUID conviteId,
                                      final Boolean resposta) {
        return conviteEquipeRepository.findById(conviteId)
                .map(convite -> {
                    var equipe = convite.getEquipe();
                    partidaRepository.findPartidaByEquipeAndFinalizadoEm(equipe, null).ifPresent(partida -> {
                        throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.RESPONDER_CONVITE_ERROR, usuario, "Você não pode entrar em uma equipe no meio de uma partida", equipe.getNome());
                    });

                    if (resposta) {
                        equipe.getUsuarios().add(usuario);
                        usuario.getEquipes().add(equipe);

                        usuarioRepository.save(usuario);
                        equipeRepository.save(equipe);
                    }

                    conviteEquipeRepository.delete(convite);
                    return mapearEquipe(equipe, usuario);
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.RESPONDER_CONVITE_ERROR, usuario, "Convite %s não encontrado", conviteId));
    }

    public UsuarioDTO enviarConvite(final Usuario usuario,
                                    final UUID equipeId,
                                    final String email) {
        return equipeRepository.findById(equipeId)
                .map(equipe ->
                {
                    CollectionUtils.emptyIfNull(equipe.getUsuarios()).stream()
                            .filter(usuarioEquipe -> email.equals(usuarioEquipe.getEmail()))
                            .findFirst()
                            .map(usuarioEquipe -> {
                                throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.ENVIAR_CONVITE_ERROR, usuario, "Usuário já está na equipe %s", equipe.getNome());
                            });

                    return Optional.ofNullable(usuarioRepository.findByEmail(email))
                            .map(usuarioConvidado -> {
                                conviteEquipeRepository.findByEquipeAndEnviadoPara(equipe, usuarioConvidado)
                                        .ifPresentOrElse(convite -> {
                                            throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.ENVIAR_CONVITE_ERROR, usuario, "Convite para equipe %s já enviado para %s %s", equipe.getNome(), usuarioConvidado.getNome(), usuarioConvidado.getSobrenome());
                                        }, () -> {
                                            conviteEquipeRepository.save(ConviteEquipe.builder()
                                                    .equipe(equipe)
                                                    .enviadoPor(usuario)
                                                    .enviadoPara(usuarioConvidado)
                                                    .build());
                                        });
                                return new UsuarioDTO(usuarioConvidado);
                            }).orElseGet(() -> {
                                CompletableFuture.runAsync(() -> mailService.enviarConviteParanoiaEEquipe(usuario, email, equipe));
                                var resposta = new UsuarioDTO();
                                resposta.setNome(email);
                                return resposta;
                            });
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.ENVIAR_CONVITE_ERROR, usuario, "Equipe %s não encontrada", equipeId));
    }

    public Boolean sairDaEquipe(final Usuario usuario,
                                final UUID equipeId) {
        return equipeRepository.findById(equipeId)
                .map(equipe -> {
                    partidaRepository.findPartidaByEquipeAndFinalizadoEm(equipe, null).ifPresent(partida -> {
                        throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.SAIR_EQUIPE_ERROR, usuario, "Você não pode sair de uma equipe no meio de uma partida", equipe.getNome());
                    });

                    if (equipe.getUsuarios().size() == 1) {
                        return deletar(usuario, equipeId);
                    } else {
                        equipe.getUsuarios().remove(usuario);
                        equipe.getUsuarios().sort(Comparator.comparing(Usuario::getCriadoEm));
                        equipe.setCriadoPor(equipe.getUsuarios().get(0));
                        equipeRepository.save(equipe);

                        usuario.getEquipes().remove(equipe);
                        usuarioRepository.save(usuario);
                        return Boolean.TRUE;
                    }
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.SAIR_EQUIPE_ERROR, usuario, "Equipe %s não encontrada", equipeId));
    }

    public Boolean removerDaEquipe(final Usuario usuarioLider,
                                   final UUID equipeId,
                                   final String usuarioHash) {
        return equipeRepository.findById(equipeId)
                .filter(equipe -> equipe.getAtiva() && equipe.getCriadoPor().getId().equals(usuarioLider.getId()))
                .map(equipe -> {
                    partidaRepository.findPartidaByEquipeAndFinalizadoEm(equipe, null).ifPresent(partida -> {
                        throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.REMOVER_MEMBRO_DA_EQUIPE_ERROR, usuarioLider, "Você não pode remover membros da equipe no meio de uma partida", equipe.getNome());
                    });

                    var usuarioRemover = CollectionUtils.emptyIfNull(equipe.getUsuarios()).stream()
                            .filter(usuarioParaRemover -> usuarioParaRemover.getHash().equals(usuarioHash))
                            .findFirst()
                            .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.REMOVER_MEMBRO_DA_EQUIPE_ERROR, usuarioLider, "Usuário %s não encontrado na equipe", usuarioHash));

                    usuarioRemover.getEquipes().remove(equipe);
                    equipe.getUsuarios().remove(usuarioRemover);

                    usuarioRepository.save(usuarioRemover);
                    equipeRepository.save(equipe);
                    return Boolean.TRUE;
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.REMOVER_MEMBRO_DA_EQUIPE_ERROR, usuarioLider, "Apenas o líder da equipe pode remover usuários", equipeId));
    }

    public List<EquipeDTO> mapearEquipes(final Usuario usuario) {
        return CollectionUtils.emptyIfNull(usuario.getEquipes()).stream()
                .filter(equipe -> !equipe.getEquipeSolo() && equipe.getAtiva())
                .map(equipe -> this.mapearEquipe(equipe, usuario))
                .collect(Collectors.toList());
    }

    private EquipeDTO mapearEquipe(final Equipe equipe,
                                   final Usuario usuario) {
        var membrosDaEquipe = CollectionUtils.emptyIfNull(usuarioRepository.findByEquipe(equipe.getId())).stream()
                .map(UsuarioEquipeDTO::new)
                .peek(usuarioEquipeDTO -> usuarioEquipeDTO.setLider(equipe.getCriadoPor().getHash().equals(usuarioEquipeDTO.getHash())))
                .collect(Collectors.toList());

        return EquipeDTO.builder()
                .id(equipe.getId())
                .nome(equipe.getNome())
                .lider(equipe.getCriadoPor().getId().equals(usuario.getId()))
                .usuarios(membrosDaEquipe)
                .build();
    }
}