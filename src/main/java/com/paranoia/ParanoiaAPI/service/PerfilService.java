package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Perfil;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.domain.enums.Niveis;
import com.paranoia.ParanoiaAPI.dto.Perfil.MedalhaDTO;
import com.paranoia.ParanoiaAPI.dto.Perfil.NivelDTO;
import com.paranoia.ParanoiaAPI.dto.Perfil.PerfilDTO;
import com.paranoia.ParanoiaAPI.dto.Perfil.RankingDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.repository.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class PerfilService {
    private static final int PONTOS_INICIAIS = 25;

    private final PerfilRepository perfilRepository;
    private final String profileImages;

    @Autowired
    PerfilService(@Value("${profile.images.path}") String profileImages,
                  final PerfilRepository perfilRepository) {
        this.profileImages = profileImages;
        this.perfilRepository = perfilRepository;
    }

    private void validarLevelUp(final Perfil perfil) {
        if (perfil.getExperiencia() >= perfil.getNivel().getExpProximoNivel()) {
            perfil.setNivel(perfil.getNivel().proximoNivel());
            perfil.setBitnoias(perfil.getBitnoias() + perfil.getNivel().getBitNoias());
            validarLevelUp(perfil);
        }
    }

    public PerfilDTO mapearPerfilDTO(Perfil perfil) {
        var perfilResposta = new PerfilDTO(perfil);
        perfilResposta.setMedalhas(mapearMedalhas(perfil.getMedalhas()));
        perfilResposta.setPosicaoRanking(posicaoRanking(perfil));
        perfilResposta.setPorcentagemProximoNivel(calcularPorcentagemProximoNivel(perfil));
        return perfilResposta;
    }

    public Boolean salvarFoto(final Usuario usuario,
                              final MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            File dir = new File(profileImages);

            if (!dir.exists())
                dir.mkdirs();

            File destiny = new File(dir.getAbsolutePath() + File.separator + usuario.getHash() + ".jpg");
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destiny));
            stream.write(bytes);
            stream.close();

            usuario.getPerfil().setFoto(usuario.getHash());
            perfilRepository.save(usuario.getPerfil());
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.SALVAR_FOTO_ERROR, usuario, e.getMessage());
        }
    }

    public RankingDTO mapearRankingDTO(Usuario usuario) {
        return RankingDTO.builder()
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .foto(usuario.getPerfil().getFoto())
                .experiencia(usuario.getPerfil().getExperiencia())
                .nivel(usuario.getPerfil().getNivel().getNivel())
                .build();
    }

    public Perfil salvar(Perfil perfil) {
        return perfilRepository.save(perfil);
    }

    private List<MedalhaDTO> mapearMedalhas(List<Medalhas> medalhasUsuario) {
        return Stream.of(Medalhas.values())
                .filter(Medalhas::getMostrarMedalha)
                .map(medalhas -> MedalhaDTO.builder()
                        .codigo(medalhas.name())
                        .nome(medalhas.getNome())
                        .descricao(medalhas.getDescricao())
                        .mostrarDescricao(medalhas.getMostrarDescricao())
                        .temMedalha(!medalhasUsuario.isEmpty() && medalhasUsuario.contains(medalhas))
                        .build())
                .collect(Collectors.toList());
    }

    private int calcularPorcentagemProximoNivel(Perfil perfil) {
        return Math.round((perfil.getExperiencia() / ((float) perfil.getNivel().getExpProximoNivel())) * 100);
    }

    private int posicaoRanking(Perfil perfil) {
        return perfilRepository.posicaoRanking(perfil.getId());
    }

    public Perfil novo() {
        return perfilRepository.save(Perfil.builder()
                .nivel(Niveis.NIVEL_1)
                .experiencia(0)
                .medalhas(new ArrayList<>())
                .bitnoias(PONTOS_INICIAIS)
                .build());
    }

    public void concederMedalhasParaEquipe(final Equipe equipe,
                                           final Set<Medalhas> medalhas) {
        medalhas.forEach(medalha -> concederMedalhaParaEquipe(equipe, medalha, Boolean.TRUE));
    }

    public void concederMedalhaParaEquipe(final Equipe equipe,
                                          final Medalhas medalhas) {
        this.concederMedalhaParaEquipe(equipe, medalhas, Boolean.TRUE);
    }

    public void concederMedalhaParaEquipe(final Equipe equipe,
                                          final Medalhas medalhas,
                                          final Boolean agregarExp) {
        equipe.getUsuarios()
                .forEach(usuarioDaEquipe -> concederMedalha(usuarioDaEquipe, medalhas, agregarExp));
    }

    public void concederMedalhas(final Usuario usuario,
                                 final Set<Medalhas> medalhas) {
        medalhas.forEach(medalha -> concederMedalha(usuario, medalha, Boolean.TRUE));
    }

    public void concederMedalha(final Usuario usuario,
                                final Medalhas medalhas) {
        concederMedalha(usuario, medalhas, Boolean.TRUE);
    }

    public void concederMedalha(final Usuario usuario,
                                final Medalhas novaMedalha,
                                final Boolean agregarExp) {
        Optional.ofNullable(usuario.getPerfil())
                .ifPresent(perfil -> {
                    emptyIfNull(perfil.getMedalhas()).stream()
                            .filter(novaMedalha::equals)
                            .findFirst()
                            .ifPresentOrElse(medalhas -> {
                                if (agregarExp) {
                                    perfil.setExperiencia(perfil.getBitnoias() + novaMedalha.getExperiencia());
                                }
                            }, () -> {
                                perfil.getMedalhas().add(novaMedalha);
                                perfil.setExperiencia(perfil.getExperiencia() + novaMedalha.getExperiencia());
                            });

                    validarLevelUp(perfil);
                    perfilRepository.save(perfil);
                });
    }
}