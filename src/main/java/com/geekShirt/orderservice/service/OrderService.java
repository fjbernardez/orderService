package com.geekShirt.orderservice.service;

import com.geekShirt.orderservice.client.CustomerServiceClient;
import com.geekShirt.orderservice.dao.JpaOrderDAO;
import com.geekShirt.orderservice.dto.AccountDto;
import com.geekShirt.orderservice.dto.OrderRequest;
import com.geekShirt.orderservice.dto.OrderResponse;
import com.geekShirt.orderservice.entities.Order;
import com.geekShirt.orderservice.entities.OrderDetail;
import com.geekShirt.orderservice.exception.AccountNotFoundExeption;
import com.geekShirt.orderservice.exception.OrderIdNotFoundExeption;
import com.geekShirt.orderservice.repositories.OrderRepository;
import com.geekShirt.orderservice.util.Constants;
import com.geekShirt.orderservice.util.ExeptionMessagesEnum;
import com.geekShirt.orderservice.util.OrderStatus;
import com.geekShirt.orderservice.util.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/*@Slf4j para log - Lombok*/
@Slf4j
@Service
public class OrderService {

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private JpaOrderDAO jpaOrderDao;

    @Autowired
    private OrderRepository orderRepository;


    private Order initOrder (OrderRequest orderRequest) {
        Order orderObj = new Order () ;

        orderObj.setOrderId(UUID.randomUUID().toString());
        orderObj.setAccountId(orderRequest.getAccountId());
        orderObj.setStatus(OrderStatus.PENDING);

        List<OrderDetail> orderDetails = orderRequest.getItems().stream()
                .map(item -> OrderDetail.builder()
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .upc(item.getUpc())
                .tax(item.getQuantity() * item.getPrice() * Constants.TAX_IMPORT)
                .build())
                .collect(Collectors.toList());

        orderObj.setDetails(orderDetails);
        orderObj.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getPrice).sum());
        orderObj.setTotalTax(orderObj.getTotalAmount() * Constants.TAX_IMPORT);
        orderObj.setTransactionDate(new Date());

        return orderObj;
    }

    /*@Transactional es nacesario para INSERT UPDATE DELETE al repositorio*/
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        //valido orderRequest recibido
        OrderValidator.validateOrder(orderRequest);

        //valido response de orderServiceConfig.getCustomerServiceUrl() - Uso de Optional.Class
        AccountDto account = customerServiceClient.findAccountById(orderRequest.getAccountId()).orElseThrow(
                ()-> new AccountNotFoundExeption(ExeptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()) );

        Order response = initOrder(orderRequest);

        return orderRepository.save(response);
    }

    public List<Order> findAllOrders(){

        return jpaOrderDao.findAll();
    }

    public Order findOrderById (String orderId) {

        Optional <Order> order = Optional.ofNullable(orderRepository.findOrderByOrderId(orderId));
        return order.orElseThrow(
                ()-> new OrderIdNotFoundExeption(ExeptionMessagesEnum.ORDER_NOT_FOUND.getValue()));
    }

    public Order findById (long id){

        Optional <Order> order = orderRepository.findById(id);
        return order.orElseThrow(
                () ->new OrderIdNotFoundExeption(ExeptionMessagesEnum.ORDER_NOT_FOUND.getValue()) );
    }

    public List<Order> findOrderByAccountId (String accountId){

        Optional <List<Order>> orders = Optional.ofNullable(orderRepository.findOrdersByAccountId(accountId));
        return orders.orElseThrow(
                () ->new OrderIdNotFoundExeption(ExeptionMessagesEnum.ORDER_NOT_FOUND.getValue()) );
    }
}