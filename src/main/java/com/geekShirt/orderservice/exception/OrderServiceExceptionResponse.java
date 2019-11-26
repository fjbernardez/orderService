package com.geekShirt.orderservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/*Primer paso para controlar la respuesta a retornar le manejo de una exepcion*/
@Data
@AllArgsConstructor
public class OrderServiceExceptionResponse {
    private String message;
    private String  details;
    private HttpStatus status;
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss" )
    private LocalDateTime localDateTime;
}
