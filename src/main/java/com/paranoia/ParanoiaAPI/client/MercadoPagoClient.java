package com.paranoia.ParanoiaAPI.client;

import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MercadoPagoClient {
    @Value("${mercadopago.api-url}")
    private String URL;
    @Value("${mercadopago.token}")
    private String TOKEN;

    private RestTemplate restTemplate = new RestTemplate();

    public Payment obterInformacoesPagamento(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Payment> response = restTemplate.exchange(
                    String.join("/", URL,  "payments", id),
                    HttpMethod.GET, entity, Payment.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
