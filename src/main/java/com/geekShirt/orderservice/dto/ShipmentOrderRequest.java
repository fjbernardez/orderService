package com.geekShirt.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentOrderRequest {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("receiptEmail")
    private String receiptEmail;

    @JsonProperty("shippingAddress")
    private AddressDto shippingAddress;
}
