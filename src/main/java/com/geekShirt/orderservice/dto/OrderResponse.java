package com.geekShirt.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

//lombok
@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private String status;
    private String accountId;
    private double totalAmount;
    private double totalTax;
    private Date transactionDate;
}
