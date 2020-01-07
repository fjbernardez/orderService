package com.geekShirt.orderservice.dto;

import com.geekShirt.orderservice.entities.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

//lombok
@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private String status;
    private String accountId;
    private double totalAmount;
    private double totalTax;
    private double totalAmountTax;
    private Date transactionDate;
    List<OrderDetailResponse> details;
}
