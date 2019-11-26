package com.geekShirt.orderservice.util;

import com.geekShirt.orderservice.dto.OrderRequest;
import com.geekShirt.orderservice.exception.IncorrectOrderRequestException;

public class OrderValidator {

    public static boolean validateOrder (OrderRequest order){
        if (order.getItems() == null || order.getItems().isEmpty()){
            throw  new IncorrectOrderRequestException(ExeptionMessagesEnum.INCORRECT_REQUEST_EMTY_ITEMS_ORDER.getValue());
        }
        return true;
    }
}
