package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.dto.Equipe.ConviteEquipeDTO;
import com.paranoia.ParanoiaAPI.dto.Equipe.EquipeDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import com.paranoia.ParanoiaAPI.service.AutenticadorService;
import com.paranoia.ParanoiaAPI.service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/equipe")
@CrossOrigin(origins = "*")
public class EquipeController {

    private final EquipeService equipeService;
    private final AutenticadorService autenticadorService;

    @Autowired
    public EquipeController(final EquipeService equipeService,
                            final AutenticadorService autenticadorService) {
        this.equipeService = equipeService;
        this.autenticadorService = autenticadorService;
    }

    @PostMapping
    public EquipeDTO criar(@RequestHeader(name = "token") String token,
                           @RequestParam(value = "nome") String nome) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.criar(usuario, nome);
    }

    @PutMapping
    public EquipeDTO editar(@RequestHeader(name = "token") String token,
                            @RequestParam(value = "equipeId") UUID equipeId,
                            @RequestParam(value = "nome") String nome) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.editar(usuario, equipeId, nome);
    }

    @DeleteMapping
    public Boolean deletar(@RequestHeader(name = "token") String token,
                           @RequestParam(value = "equipeId") UUID equipeId) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.deletar(usuario, equipeId);
    }

    @GetMapping
    public List<EquipeDTO> obterEquipes(@RequestHeader(name = "token") String token) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.mapearEquipes(usuario);
    }

    @GetMapping(value = "/convites")
    public List<ConviteEquipeDTO> obterConvites(@RequestHeader(name = "token") String token) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.obterConvites(usuario);
    }

    @PostMapping(value = "/enviar_convite")
    public UsuarioDTO enviarConvite(@RequestHeader(name = "token") String token,
                                    @RequestParam(value = "equipeId") UUID equipeId,
                                    @RequestParam(value = "email") String email) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.enviarConvite(usuario, equipeId, email);
    }

    @PutMapping(value = "/responder_convite")
    public EquipeDTO responderConvite(@RequestHeader(name = "token") String token,
                                      @RequestParam(value = "resposta") Boolean resposta,
                                      @RequestParam(value = "conviteId") UUID conviteId) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.responderConvite(usuario, conviteId, resposta);
    }

    @PutMapping(value = "/sair")
    public Boolean sairDaEquipe(@RequestHeader(name = "token") String token,
                                @RequestParam(value = "equipeId") UUID equipeId) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.sairDaEquipe(usuario, equipeId);
    }

    @PutMapping(value = "/remover_membro")
    public Boolean removerMembroDaEquipe(@RequestHeader(name = "token") String token,
                                         @RequestParam(value = "equipeId") UUID equipeId,
                                         @RequestParam(value = "usuarioHash") String usuarioHash) {
        var usuario = this.autenticadorService.autorizar(token);
        return equipeService.removerDaEquipe(usuario, equipeId, usuarioHash);
    }
}