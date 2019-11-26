package com.geekShirt.orderservice.handler;

import com.geekShirt.orderservice.exception.AccountNotFoundExeption;
import com.geekShirt.orderservice.exception.IncorrectOrderRequestException;
import com.geekShirt.orderservice.exception.OrderServiceExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sun.rmi.transport.ObjectTable;

import java.time.LocalDateTime;

@ControllerAdvice
@RestController
public class OrderServicesExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerAllExceptions (Exception exception, WebRequest request) {
        OrderServiceExceptionResponse response = new OrderServiceExceptionResponse(exception.getMessage(),
                request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now() );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(IncorrectOrderRequestException.class)
    public ResponseEntity<Object>  handlerIncorrectOrderRequestException (Exception exception, WebRequest request) {
        OrderServiceExceptionResponse response = new OrderServiceExceptionResponse(exception.getMessage(),
                request.getDescription(false), HttpStatus.BAD_REQUEST, LocalDateTime.now() );
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(AccountNotFoundExeption.class)
    public ResponseEntity<Object> accountNotFoundExeption (Exception exception, WebRequest request) {
        OrderServiceExceptionResponse response = new OrderServiceExceptionResponse(exception.getMessage(),
                request.getDescription(false), HttpStatus.NOT_FOUND, LocalDateTime.now() );
        return new ResponseEntity<>(response, response.getStatus());
    }
}
