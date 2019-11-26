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
import com.geekShirt.orderservice.util.ExeptionMessagesEnum;
import com.geekShirt.orderservice.util.OrderStatus;
import com.geekShirt.orderservice.util.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
                .tax(item.getQuantity() * item.getPrice())
                .order(orderObj).build())
                .collect(Collectors.toList());

        orderObj.setDetails(orderDetails);
        orderObj.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getPrice).sum());
        orderObj.setTotalTax(orderObj.getTotalAmount() * 0.16);
        orderObj.setTransactionDate(new Date());

        return orderObj;
    }

    public Order createOrder(OrderRequest orderRequest) {
        //valido orderRequest recibido
        OrderValidator.validateOrder(orderRequest);

        //valido response de orderServiceConfig.getCustomerServiceUrl() - Uso de Optional.Class
        AccountDto account = customerServiceClient.findAccountById(orderRequest.getAccountId()).orElseThrow(
                ()-> new AccountNotFoundExeption(ExeptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()) );

/* consumo de servicios - CustomerServiceClient

        //instancio dummyAcount
        AccountDto dummyAccount  = customerServiceClient.createDummyAccount();

        //post cuenta dummy
        //dummyAcount = customerServiceClient.createAccount(dummyAcount);
        dummyAccount = customerServiceClient.createAccountBody(dummyAccount);
        log.info("Response (antesDeActualizar): " + dummyAccount.getAddress().getZipCode() );

        //Ahora put. Para eso primero genero una modificacion, para que corroborar la actualizacion
        dummyAccount.getAddress().setZipCode("zipCodeModified");
        customerServiceClient.updateAccount(dummyAccount);
        //invoco el objeto para ver la actualizacion.
        //NOTA: findAccountById(orderRequest.getAccountId()) se mantiene por que busco verificar la actualizacion
        AccountDto updateAccount = customerServiceClient.findAccountById(orderRequest.getAccountId());
        //logeo el resultado
        //log.info("Response (actualizado): " + updateAccount.getAddress().getZipCode() );
        log.info("Response (actualizado):\n" + updateAccount.toString() );

        //Ahora elimino la cuenta creada
        customerServiceClient.deleteAccount(dummyAccount);
*/
        Order response = initOrder(orderRequest);

        return jpaOrderDao.save(response);
    }

    public List<Order> findAllOrders(){

        return jpaOrderDao.findAll();



//        List<Order> orderList = new ArrayList();
//
//        Order response = new Order();
//        response.setAccountId("999819");
//        response.setOrderId("11123");
//        //response.setStatus("pending");
//        response.setStatus(OrderStatus.PENDING);
//        response.setTotalAmount(100.00);
//        response.setTotalTax(10.00);
//        response.setTransactionDate(new Date());
//
//        Order response02 = new Order();
//        response02.setAccountId("999819");
//        response02.setOrderId("11124");
//        //response02.setStatus("pending");
//        response02.setStatus(OrderStatus.PENDING);
//        response02.setTotalAmount(120.00);
//        response02.setTotalTax(12.00);
//        response02.setTransactionDate(new Date());
//
////      agrego a la lista
//        orderList.add(response);
//        orderList.add(response02);
//        return orderList;
    }

    public Order findOrderById (String orderId){

        return jpaOrderDao.findByOrderId(orderId).orElseThrow(
                () -> new OrderIdNotFoundExeption(ExeptionMessagesEnum.ORDER_ID_NOT_FOUND.getValue()) );
//        Order response = new Order();
//        response.setAccountId("999819");
//        response.setOrderId(orderId);
//        //response.setStatus("pending");|
//        response.setStatus(OrderStatus.PENDING);
//        response.setTotalAmount(100.00);
//        response.setTotalTax(10.00);
//        response.setTransactionDate(new Date());
//        return response;
    }
}
