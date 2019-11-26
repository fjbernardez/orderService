package com.geekShirt.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//determina status http para le manejo de la exception
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IncorrectOrderRequestException extends RuntimeException {
    public IncorrectOrderRequestException(String message) {
        super(message);
    }
}
