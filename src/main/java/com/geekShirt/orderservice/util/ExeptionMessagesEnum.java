package com.geekShirt.orderservice.util;

public enum ExeptionMessagesEnum {
    ACCOUNT_NOT_FOUND ("mensaje asociado al enum: ACCOUNT_NOT_FOUND."),
    INCORRECT_REQUEST_EMTY_ITEMS_ORDER("mensaje asociado al enum:INCORRECT_REQUEST_EMTY_ITEMS_ORDER."),
    ORDER_NOT_FOUND ("mensaje asociado al enum:ORDER_ID_NOT_FOUND."),
    PAYMENT_ADDED_NOT_ACCEPTED ("mensaje asociado al enum:PAYMENT_ADDED_NOT_ACCEPTED.");

    ExeptionMessagesEnum(String msj ){
        value = msj;
    }

    private final String value ;

    public String getValue() {
        return value;
    }
}
