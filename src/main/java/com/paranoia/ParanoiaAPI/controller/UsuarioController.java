package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioCriacaoDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioDTO;
import com.paranoia.ParanoiaAPI.dto.Usuario.UsuarioLoginDTO;
import com.paranoia.ParanoiaAPI.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping(value = "/try_me")
    public Boolean testConnection() {
        return Boolean.TRUE;
    }

    @PutMapping(value = "/login")
    public UsuarioDTO login(@RequestBody final UsuarioLoginDTO usuarioLogin) {
        return usuarioService.login(usuarioLogin);
    }

    @PutMapping(value = "/logout")
    public void logout(@RequestHeader(name = "token") final String token) {
        usuarioService.logout(token);
    }

    @PostMapping
    public Boolean criar(@RequestBody final UsuarioCriacaoDTO userDTO) {
        return usuarioService.criar(userDTO);
    }

    @PostMapping(value = "/criar_por_json")
    public Boolean criarPorJSON(@RequestBody final List<UsuarioCriacaoDTO> usuariosDTO) {
        return usuarioService.criarPorJSON(usuariosDTO);
    }

    @GetMapping(value = "/verificar_email")
    public Boolean verificarIndicadoPor(@RequestParam(name = "indicadoPor") final String indicadoPor) {
        return usuarioService.verificarEmail(indicadoPor);
    }

    @PutMapping(value = "/confirmar_email")
    public Boolean confirmarEmail(@RequestParam(name = "codigoConfirmacaoEmail") final String codigoConfirmacaoEmail) {
        return usuarioService.confirmarEmail(codigoConfirmacaoEmail);
    }

    @PutMapping(value = "/resetar_senha")
    public Boolean resetarSenha(@RequestParam(name = "email") final String email) {
        return usuarioService.resetarSenha(email);
    }

    @PutMapping(value = "/trocar_senha")
    public Boolean trocarSenha(@RequestParam(name = "codigoResetarSenha") final String codigoResetarSenha,
                               @RequestParam(name = "senha") final String senha) {
        return usuarioService.trocarSenha(codigoResetarSenha, senha);
    }
}