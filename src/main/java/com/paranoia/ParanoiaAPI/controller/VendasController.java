package com.paranoia.ParanoiaAPI.controller;

import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.MercadoPagoStatusEnum;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaCriacaoDTO;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.service.AutenticadorService;
import com.paranoia.ParanoiaAPI.service.MercadoPagoService;
import com.paranoia.ParanoiaAPI.service.VendaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/venda")
@CrossOrigin(origins = "*")
public class VendasController {

    private final VendaService vendaService;
    private final MercadoPagoService mercadoPagoService;
    private final AutenticadorService autenticadorService;

    public VendasController(final VendaService vendaService,
                            final MercadoPagoService mercadoPagoService,
                            final AutenticadorService autenticadorService) {
        this.vendaService = vendaService;
        this.mercadoPagoService = mercadoPagoService;
        this.autenticadorService = autenticadorService;
    }

    @PostMapping
    public VendaDTO criar(@RequestHeader(name = "token") String token,
                          @RequestBody VendaCriacaoDTO vendaDTO) {
        return Optional.ofNullable(this.autenticadorService.autorizar(token))
                .map(usuario -> vendaService.criar(usuario, vendaDTO))
                .orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.CRIAR_VENDA_ERROR, null, "Erro ao criar venda, usuário não encontrado"));
    }

    @GetMapping
    public List<LocalDateTime> obter() {
        return this.vendaService.obter();
    }

    @PutMapping("/alterar/mercadoPago")
    public void alterarmercadoPago(@RequestParam(value = "vendaId") UUID vendaId,
                                   @RequestParam(value = "mercadoPagoId") String mercadoPagoId,
                                   @RequestBody String htmlContrato) {
        vendaService.alterarMercadoPago(vendaId, mercadoPagoId, htmlContrato);
    }

    @GetMapping("/bitnoias")
    public int obterBitnoias(@RequestHeader(name = "token") String token) {
        return Optional.ofNullable(this.autenticadorService.autorizar(token))
                .map(usuario -> usuario.getPerfil().getBitnoias())
                .orElseGet(() -> 0);
    }

    @GetMapping("/verificar-cupom")
    public BigDecimal verificarCupom(@RequestParam("cupom") String cupom) {
        return this.vendaService.verificarCupom(cupom);
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<String> mercadopago(@RequestParam("vendaId") UUID vendaId,
                                              @RequestBody Map<String, Object> payload) {
        this.mercadoPagoService.validarRetorno(payload)
                .ifPresent(status -> {
                    if (StringUtils.isEmpty(status))
                        return;

                    this.vendaService.confirmar(vendaId, MercadoPagoStatusEnum.valueOf(status.toUpperCase()));
                });
        return ResponseEntity.ok("Received");
    }
}