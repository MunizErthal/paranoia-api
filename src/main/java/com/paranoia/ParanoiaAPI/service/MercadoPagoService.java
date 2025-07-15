package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.client.MercadoPagoClient;
import com.paranoia.ParanoiaAPI.dto.MercadoPago.DataPayloadDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;


@Service
public class MercadoPagoService {
    private final MercadoPagoClient mercadoPagoClient;

    private static final String TIPO_PAGAMENTO = "payment";

    public MercadoPagoService(final MercadoPagoClient mercadoPagoClient) {
        this.mercadoPagoClient = mercadoPagoClient;
    }

    public Optional<String> validarRetorno(@RequestBody Map<String, Object> payload) {
        return Optional.ofNullable((String) payload.get("type"))
                .map(tipo -> {
                    if (TIPO_PAGAMENTO.equals(tipo)) {
                        var dataPayload = new DataPayloadDTO((Map<String, Object>) payload.get("data"));
                        if (!isNull(dataPayload.getId())) {
                            var informacoesPagamento = this.mercadoPagoClient.obterInformacoesPagamento(dataPayload.getId());
                            if (isNull(informacoesPagamento)) {
                                return "";
                            }
                            return informacoesPagamento.getStatus();
                        }
                    }
                    return "";
                });
    }
}
