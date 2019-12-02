package com.geekShirt.orderservice.service;

import com.geekShirt.orderservice.client.PaymentServiceClient;
import com.geekShirt.orderservice.dto.*;
import com.geekShirt.orderservice.entities.Order;
import com.geekShirt.orderservice.util.CurrencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessorService {

    @Autowired
    private PaymentServiceClient paymentClient;

    /**/
    public Confirmation processPayment(Order order, AccountDto account) {
        /*crea nuevo objeto dto con base en la tarjeta de credito*/
        PaymentDetailsDto paymentDetailsDto = createPaymentDetails(account);
        /*crea paymentRequest basado en la orden enviada*/
        PaymentRequest paymentRequest = createPaymentRequest(paymentDetailsDto, order);
        /*consultamamos a paymentClient con el metodo authorize.Retorna una confirmacion.*/
        return paymentClient.authorize(paymentRequest);
    }

    private PaymentRequest createPaymentRequest(PaymentDetailsDto paymentDetailsDto, Order order) {
        return PaymentRequest.builder()
                .payment(paymentDetailsDto)
                .accountId(order.getAccountId())
                .amount(order.getTotalAmount())
                .orderId(order.getOrderId())
                .currency(CurrencyType.USD.name())
                .build();
    }

    private PaymentDetailsDto createPaymentDetails(AccountDto account) {
        CreditCardDto cardDetails = account.getCreditCard();
        return PaymentDetailsDto.builder().cardNumber(cardDetails.getNumber())
                .cardCode(cardDetails.getCcv())
                .expirationMonth(cardDetails.getExpirationMonth())
                .expirationYear(cardDetails.getExpirationYear())
                .nameOnCard(cardDetails.getNameOnCard())
                .method(cardDetails.getType())
                .build();
    }
}