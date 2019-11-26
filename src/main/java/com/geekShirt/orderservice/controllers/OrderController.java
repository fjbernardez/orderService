package com.geekShirt.orderservice.controllers;

import com.geekShirt.orderservice.dto.OrderRequest;
import com.geekShirt.orderservice.dto.OrderResponse;
import com.geekShirt.orderservice.entities.Order;
import com.geekShirt.orderservice.service.OrderService;
import com.geekShirt.orderservice.util.EntityDtoConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private EntityDtoConverter converter;

    @ApiOperation(value = "Retorna todas las ordenes", notes = "Descripción más detallada, etc")
    @GetMapping(value = "order")
    public ResponseEntity<List<OrderResponse>> findAll(){
        List<Order> orders = orderService.findAllOrders();

        return new ResponseEntity<>(converter.convertEntityToDto(orders), HttpStatus.OK);
    }

    @ApiOperation(value = "Retorna orden basado en ID", notes = "Notas...")
    @GetMapping(value = "order/{orderId}")
    public ResponseEntity<OrderResponse> findById(@PathVariable String orderId){
        Order order = orderService.findOrderById(orderId);
        return new ResponseEntity<>(converter.convertEntityToDto(order),HttpStatus.OK);
    }

    @ApiOperation(value = "Crea una orden", notes = "notas...")
    @PostMapping(value = "order/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest payLoad) {
        Order order = orderService.createOrder(payLoad);
        return new ResponseEntity<>(converter.convertEntityToDto(order),HttpStatus.OK);
    }
}