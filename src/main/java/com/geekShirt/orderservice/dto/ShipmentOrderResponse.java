package com.geekShirt.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentOrderResponse implements Serializable {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("trackingId")
    private String trackingId;

    @JsonProperty("receiptEmail")
    private String receiptEmail;

    @JsonProperty("shippingStatus")
    private String shippingStatus;

    @JsonProperty("address")
    private AddressDto address;

    @JsonProperty("shippingDate")
    private Date shippingDate;

    @JsonProperty("deliveredDate")
    private Date deliveredDate;
}
