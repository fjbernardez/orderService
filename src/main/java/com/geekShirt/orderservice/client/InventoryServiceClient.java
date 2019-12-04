package com.geekShirt.orderservice.client;

import com.geekShirt.orderservice.config.OrderServiceConfig;
import com.geekShirt.orderservice.dto.LineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class InventoryServiceClient {
    private RestTemplate restTemplate;

    @Autowired
    private OrderServiceConfig serviceConfig;

    public InventoryServiceClient(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    public void updateInventory(List<LineItem> requestItems) {
        ResponseEntity<String> response = restTemplate.postForEntity(
                serviceConfig.getInventoryServiceUrl(), requestItems, String.class);
    }
}
