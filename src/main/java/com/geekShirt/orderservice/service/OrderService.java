package com.geekShirt.orderservice.service;

import com.geekShirt.orderservice.client.CustomerServiceClient;
import com.geekShirt.orderservice.client.InventoryServiceClient;
import com.geekShirt.orderservice.dao.JpaOrderDAO;
import com.geekShirt.orderservice.dto.*;
import com.geekShirt.orderservice.entities.Order;
import com.geekShirt.orderservice.entities.OrderDetail;
import com.geekShirt.orderservice.exception.AccountNotFoundExeption;
import com.geekShirt.orderservice.exception.OrderIdNotFoundExeption;
import com.geekShirt.orderservice.exception.PaymentNotAcceptedException;
import com.geekShirt.orderservice.producer.ShippingOrderProducer;
import com.geekShirt.orderservice.repositories.OrderRepository;
import com.geekShirt.orderservice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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

    @Autowired
    private PaymentProcessorService paymentService;

    @Autowired
    private InventoryServiceClient inventoryClient;

    /*tomo el bean para poder realizar el envio de los mensajes por rabbitMQ*/
    @Autowired
    private ShippingOrderProducer shipmentMessageProducer;

    @Autowired
    private OrderMailService mailService;


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
        orderObj.setTotalAmountTax(orderObj.getTotalAmount() + orderObj.getTotalTax());
        orderObj.setTransactionDate(new Date());

        return orderObj;
    }

    /*@Transactional es nacesario para INSERT UPDATE DELETE al repositorio*/
    @Transactional
    public Order createOrder(OrderRequest orderRequest) throws PaymentNotAcceptedException {
        //valido orderRequest recibido
        OrderValidator.validateOrder(orderRequest);

        //valido response de orderServiceConfig.getCustomerServiceUrl() - Uso de Optional.Class
        AccountDto account = customerServiceClient.findAccountById(orderRequest.getAccountId()).orElseThrow(
                ()-> new AccountNotFoundExeption(ExeptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue()) );

        Order response = initOrder(orderRequest);
        Confirmation confirmation = paymentService.processPayment(response, account);

        log.info("Payment Confirmation: {}", confirmation);
        /*obtengo el status*/
        String paymentStatus = confirmation.getTransactionStatus();
        /*actualizo el status de la orden acorde a la consulta*/
        response.setPaymentStatus(OrderPaymentStatus.valueOf(paymentStatus));

        if (paymentStatus.equals(OrderPaymentStatus.DENIED.name())) {
            response.setStatus(OrderStatus.NA);
            orderRepository.save(response);
            throw new PaymentNotAcceptedException(ExeptionMessagesEnum.PAYMENT_ADDED_NOT_ACCEPTED.getValue());
        }

        log.info("Updating Inventory: {}", orderRequest.getItems());

        inventoryClient.updateInventory(orderRequest.getItems());

        /*envio a INBOUND_SHIPMENT_ORDER - RabbitMQ*/
        log.info("Sending Request to Shipping Service.");
        shipmentMessageProducer.send(response.getOrderId(), account);

        return orderRepository.save(response);
    }

    @Cacheable(value = "orders")
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

    // **********************************CACHE***************************************************
    /*
    Esta anotacion indica que este acceso a la base de datos opera con memoria cache.
    Los parametros definen lo valores de un mapa que administrara, en este caso, Redis.
    Nombre del mapa es la propiedad value="ordersAccount", y el parametro accountId es
    la key="#accountId" del mapa.
    Recibi el String accountId y retorna el valor del mapa, la List<Order>
    */
    @Cacheable(value = "ordersAccount", key = "#accountId")
    public List<Order> findOrderByAccountId (String accountId){

        Optional <List<Order>> orders = Optional.ofNullable(orderRepository.findOrdersByAccountId(accountId));
        return orders.orElseThrow(
                () ->new OrderIdNotFoundExeption(ExeptionMessagesEnum.ORDER_NOT_FOUND.getValue()) );
    }

    /*metodo contenido en handler de recepcion de mensajes*/
    public void updateShipmentOrder(ShipmentOrderResponse response) {
        try {
            Order order = findOrderById(response.getOrderId());
            order.setStatus(OrderStatus.valueOf(response.getShippingStatus()));
            orderRepository.save(order);
            mailService.sendEmail(order, response);
        }
        catch(OrderIdNotFoundExeption orderNotFound) {
            log.info("The Following Order was not found: {} with tracking Id: {}", response.getOrderId(), response.getTrackingId());
        }
        catch(Exception e) {
            log.info("An error occurred sending email: " + e.getMessage());
        }
    }
}