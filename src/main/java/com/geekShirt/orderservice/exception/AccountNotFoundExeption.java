package com.geekShirt.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundExeption extends RuntimeException {
    public AccountNotFoundExeption (String message){
        super (message);
    }
}
