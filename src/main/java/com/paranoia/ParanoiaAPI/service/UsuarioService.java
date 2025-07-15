package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Perfil;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.dto.Partida.PartidaDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioCriacaoDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioLoginDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.repository.EquipeRepository;
import com.paranoia.ParanoiaAPI.repository.UsuarioRepository;
import com.paranoia.ParanoiaAPI.utils.MD5Hashing;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EquipeRepository equipeRepository;
    private final UsuarioTokenService usuarioTokenService;
    private final PerfilService perfilService;
    private final MailService mailService;
    private final HistoricoService historicoService;

    @Autowired
    public UsuarioService(final UsuarioRepository usuarioRepository,
                          final EquipeRepository equipeRepository,
                          final HistoricoService historicoService,
                          final MailService mailService,
                          final PerfilService perfilService,
                          final UsuarioTokenService usuarioTokenService) {
        this.mailService = mailService;
        this.equipeRepository = equipeRepository;
        this.perfilService = perfilService;
        this.historicoService = historicoService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioTokenService = usuarioTokenService;
    }

    public UsuarioDTO login(final UsuarioLoginDTO usuarioLogin) {
        var senhaHash = MD5Hashing.generateHash(usuarioLogin.getSenha());
        return Optional.ofNullable(usuarioRepository.findByEmailAndSenha(usuarioLogin.getEmail(), senhaHash))
                .map(usuario -> {
                    Optional.of(usuario.getEmailConfirmado())
                            .filter(emailConfirmado -> emailConfirmado)
                            .orElseThrow(() -> new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.LOGIN_ERROR, usuario, "O email %s ainda não foi confirmado.", usuarioLogin.getEmail()));

                    // Salvar token
                    var token = MD5Hashing.generateHash(LocalDateTime.now() + usuario.getEmail());
                    var usuarioToken = usuarioTokenService.registrarToken(usuarioLogin, usuario, token);

                    var usuarioDTO = new UsuarioDTO(usuario);
                    usuarioDTO.setToken(usuarioToken.getToken());
                    usuarioDTO.setPartidasEmAndamento(CollectionUtils.emptyIfNull(usuario.getPartidas()).stream()
                            .filter(partida -> isNull(partida.getFinalizadoEm()))
                            .map(PartidaDTO::new)
                            .collect(Collectors.toList()));
                    return usuarioDTO;
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.LOGIN_ERROR, null, "As credenciais informadas estão incorretas."));
    }

    public void logout(final String token) {
        Optional.ofNullable(usuarioTokenService.validarToken(token))
                .ifPresentOrElse(usuarioTokenService::delete, () -> {
                    throw new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.LOGOUT_ERROR, null, "Conexão não encontrada", token);
                });
    }

    public Boolean criar(UsuarioCriacaoDTO userDTO) {
        Optional.ofNullable(usuarioRepository.findByEmail(userDTO.getEmail()))
                .ifPresent(usuario -> {
                    throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CADASTRO_ERROR, null, "O e-mail %s já está em uso", userDTO.getEmail());
                });

        Optional.ofNullable(usuarioRepository.findByCpf(userDTO.getCpf()))
                .ifPresent(usuario -> {
                    throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CADASTRO_ERROR, null, "O cpf %s já está em uso", userDTO.getCpf());
                });

        var novoUsuario = new Usuario(userDTO);

        novoUsuario.setPerfil(perfilService.novo());
        novoUsuario.setEquipes(new ArrayList<>());
        novoUsuario.setSenha(MD5Hashing.generateHash(userDTO.getSenha()));
        novoUsuario.setHash(MD5Hashing.generateHash(novoUsuario.getEmail()));
        novoUsuario.setCodigoConfirmacaoEmail(MD5Hashing.generateHash(LocalDateTime.now().toString()));

        return Optional.of(usuarioRepository.save(novoUsuario))
                .map(usuario -> {
                    Equipe equipeSolo = criarEquipeSolo(usuario);
                    usuario.getEquipes().add(equipeSolo);

                    Optional.ofNullable(userDTO.getEquipeId())
                            .flatMap(equipeRepository::findById)
                            .ifPresent(equipe -> {
                                equipe.getUsuarios().add(usuario);
                                equipeRepository.save(equipe);
                                usuario.getEquipes().add(equipe);
                            });

                    Optional.ofNullable(usuarioRepository.findByEmail(userDTO.getIndicadoPor()))
                            .ifPresent(indicadoPor -> perfilService.concederMedalha(indicadoPor, Medalhas.QUEM_INDICA_AMIGO_E, Boolean.TRUE));

                    CompletableFuture.runAsync(() -> mailService.enviarConfirmacaoDeEmail(usuario));
                    return Boolean.TRUE;
                })
                .orElseThrow(() -> new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.CADASTRO_ERROR, null, "Erro ao criar novo usuário para o DTO %s", userDTO.toString()));
    }

    public Boolean criarPorJSON(List<UsuarioCriacaoDTO> usuarios) {
        usuarios.forEach(usuarioDTO -> {
            Optional.ofNullable(usuarioRepository.findByEmail(usuarioDTO.getEmail()))
                    .ifPresent(usuario -> {
                        throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CADASTRO_ERROR, null, "O e-mail %s já está em uso", usuarioDTO.getEmail());
                    });

            Optional.ofNullable(usuarioRepository.findByCpf(usuarioDTO.getCpf()))
                    .ifPresent(usuario -> {
                        throw new ParanoiaException(HttpStatus.CONFLICT, HistoricoAcoes.CADASTRO_ERROR, null, "O cpf %s já está em uso", usuarioDTO.getEmail());
                    });

            var novoUsuario = new Usuario(usuarioDTO);
            var perfil = new Perfil(usuarioDTO.getPerfilCriacaoJSON());

            if (nonNull(usuarioDTO.getMedalhasIdsCriacaoJSON()) && !usuarioDTO.getMedalhasIdsCriacaoJSON().isEmpty()) {
                perfil.setMedalhas(new ArrayList<>(Medalhas.getByIdentificadorBaseAntiga(usuarioDTO.getMedalhasIdsCriacaoJSON())));
            }

            perfilService.salvar(perfil);

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            novoUsuario.setCriadoEm(LocalDateTime.parse(usuarioDTO.getCriadoEmCriacaoJSON(), formato));
            novoUsuario.setPerfil(perfil);
            novoUsuario.setEquipes(new ArrayList<>());
            novoUsuario.setSenha(usuarioDTO.getSenha());
            novoUsuario.setHash(MD5Hashing.generateHash(novoUsuario.getEmail()));
            novoUsuario.setEmailConfirmado(Boolean.TRUE);

            var usuario = usuarioRepository.save(novoUsuario);
            Equipe equipeSolo = criarEquipeSolo(usuario);
            usuario.getEquipes().add(equipeSolo);
            usuarioRepository.save(novoUsuario);
        });

        return Boolean.TRUE;
    }

    public Boolean verificarEmail(final String email) {
        return Optional.ofNullable(usuarioRepository.findByEmail(email))
                .isPresent();
    }

    public Boolean resetarSenha(final String email) {
        return Optional.ofNullable(usuarioRepository.findByEmail(email))
                .map(usuario -> {
                    var codigoResetarSenha = MD5Hashing.generateHash(LocalDateTime.now() + usuario.getEmail());
                    usuario.setCodigoResetarSenha(codigoResetarSenha);
                    usuarioRepository.save(usuario);

                    CompletableFuture.runAsync(() -> mailService.enviarEmailResetarSenha(usuario));
                    return Boolean.TRUE;
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.RESETAR_SENHA_ERROR, null, "Usuário não encontrado com email %s", email));
    }

    public Usuario obterUsuarioPorEmail(final String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Boolean trocarSenha(final String codigoResetarSenha,
                              final String senha) {
        return Optional.ofNullable(usuarioRepository.findByCodigoResetarSenha(codigoResetarSenha))
                .map(usuario -> {
                    usuario.setSenha(MD5Hashing.generateHash(senha));
                    usuario.setCodigoResetarSenha(null);
                    usuarioRepository.save(usuario);
                    return Boolean.TRUE;
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.UNAUTHORIZED, HistoricoAcoes.RESETAR_SENHA_ERROR, null, "Código de recuperação de senha inválido"));
    }

    public Boolean confirmarEmail(final String codigoConfirmacaoEmail) {
        return Optional.ofNullable(usuarioRepository.findByCodigoConfirmacaoEmail(codigoConfirmacaoEmail))
                .map(usuario -> {
                    usuario.setEmailConfirmado(Boolean.TRUE);
                    usuario.setCodigoConfirmacaoEmail(null);
                    usuarioRepository.save(usuario);
                    return Boolean.TRUE;
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.CONFIRMACAO_EMAIL_ERROR, null, "Código de confirmação de e-mail inválido"));
    }

    public List<Usuario> buscarRanking(int quantidade) {
        return this.usuarioRepository.buscarRanking(quantidade);
    }

    public List<Usuario> getByEquipe(Equipe equipe) {
        return usuarioRepository.findByEquipe(equipe.getId());
    }

    public Usuario salvar(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    private Equipe criarEquipeSolo(Usuario usuario) {
        Equipe equipe = Equipe.builder()
                .criadoPor(usuario)
                .ativa(Boolean.TRUE)
                .nome(usuario.getHash())
                .equipeSolo(Boolean.TRUE)
                .usuarios(List.of(usuario))
                .criadoEm(LocalDateTime.now())
                .build();
        return equipeRepository.save(equipe);
    }
}
