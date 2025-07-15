package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.Medalhas;
import com.paranoia.ParanoiaAPI.dto.Perfil.PerfilDTO;
import com.paranoia.ParanoiaAPI.dto.Perfil.RankingDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.service.AutenticadorService;
import com.paranoia.ParanoiaAPI.service.HistoricoService;
import com.paranoia.ParanoiaAPI.service.PerfilService;
import com.paranoia.ParanoiaAPI.service.UsuarioService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {
    private final PerfilService perfilService;
    private final UsuarioService usuarioService;
    private final AutenticadorService autenticadorService;
    private final HistoricoService historicoService;
    private final Set<String> adminEmails;

    @Autowired
    public PerfilController(@Value("${admin.emails}") Set<String> adminEmails,
                            final PerfilService perfilService,
                            final UsuarioService usuarioService,
                            final HistoricoService historicoService,
                            final AutenticadorService autenticadorService) {
        this.adminEmails = adminEmails;
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.historicoService = historicoService;
        this.autenticadorService = autenticadorService;
    }

    @GetMapping
    public PerfilDTO buscarPerfil(@RequestHeader(name = "token") final String token) {
        var usuario = this.autenticadorService.autorizar(token);
        return Optional.ofNullable(usuario.getPerfil())
                .map(perfilService::mapearPerfilDTO)
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.BUSCAR_PERFIL_ERROR, null, "Perfil não encontrado"));
    }

    @GetMapping("/ranking")
    public List<RankingDTO> buscarRanking(@RequestParam(value = "size") int quantidade) {
        return CollectionUtils.emptyIfNull(this.usuarioService.buscarRanking(quantidade)).stream()
                .map(perfilService::mapearRankingDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/foto")
    public Boolean salvarFoto(@RequestHeader(name = "token") String token,
                              @RequestParam(value = "file") MultipartFile arquivo) {
        Usuario usuario = this.autenticadorService.autorizar(token);
        return this.perfilService.salvarFoto(usuario, arquivo);
    }

    @PutMapping("/conceder-medalha")
    public Boolean concederMedalhas(@RequestHeader(name = "token") String token,
                                    @RequestParam(value = "emailUsuario") String emailUsuario,
                                    @RequestParam(value = "medalhas") List<String> medalhas) {
        return Optional.ofNullable(this.autenticadorService.autorizar(token))
                .filter(admin -> adminEmails.contains(admin.getEmail()))
                .map(admin -> Optional.ofNullable(usuarioService.obterUsuarioPorEmail(emailUsuario))
                        .map(usuario -> {
                            perfilService.concederMedalhas(usuario, Medalhas.getByNames(medalhas));
                            return Boolean.TRUE;
                        })
                        .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.CONCEDER_MEDALHAS_ERROR, null, "Medalha não pode ser concedida")))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.CONCEDER_MEDALHAS_ERROR, null, "Medalha não pode ser concedida"));
    }
}