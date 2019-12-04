package com.geekShirt.orderservice.client;

import com.geekShirt.orderservice.config.OrderServiceConfig;
import com.geekShirt.orderservice.dto.Confirmation;
import com.geekShirt.orderservice.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/*@Slf4j propiedades de logeo de Lombok*/
@Slf4j
@Component
public class PaymentServiceClient {

    private RestTemplate restTemplate;

    @Autowired
    private OrderServiceConfig serviceConfig;

    public PaymentServiceClient(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    public Confirmation authorize(PaymentRequest request) {
        Confirmation confirmation = restTemplate.postForObject(
                serviceConfig.getPaymentServiceUrl(), request, Confirmation.class);

        Confirmation
                confirmation1 = confirmation;
        return confirmation1;
    }
}