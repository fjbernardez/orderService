package com.geekShirt.orderservice.util;

import com.geekShirt.orderservice.dto.OrderResponse;
import com.geekShirt.orderservice.entities.Order;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoConverter {
    @Autowired
    private ModelMapper modelMapper;

    /*Metodo que recibe el objeto y lo transforma*/
    public OrderResponse convertEntityToDto (Order order){

        /*Toma todos los datos de order, y pasa los datos de un objeto a otro (con mismo nombre)*/
        return modelMapper.map(order,OrderResponse.class);
    }

    public List<OrderResponse> convertEntityToDto(List<Order> orders){
        return orders.stream()
                .map(order -> convertEntityToDto(order))
                .collect(Collectors.toList());
    }
}