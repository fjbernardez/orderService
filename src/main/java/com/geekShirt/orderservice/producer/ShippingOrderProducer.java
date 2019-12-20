package com.geekShirt.orderservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekShirt.orderservice.dto.AccountDto;
import com.geekShirt.orderservice.dto.CustomerDto;
import com.geekShirt.orderservice.dto.ShipmentOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShippingOrderProducer {
    /*clase para el envio de mensajes a travez de ella*/
    @Autowired
    private RabbitTemplate template;

    /*cola para la salida de mensajes*/
    @Qualifier(value = "outbound")
    private Queue queue;
    /*metodo para el envio de los mensajes*/
    public void send(String orderId, AccountDto account) {
        ShipmentOrderRequest shipmentRequest = new ShipmentOrderRequest();
        /*convertir el objeto en Json*/
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CustomerDto customer = account.getCustomer();
            String shipmentReceiver = customer.getLastName() + ", " + customer.getFirstName();

            shipmentRequest.setOrderId(orderId);
            shipmentRequest.setName(shipmentReceiver);
            shipmentRequest.setReceiptEmail(customer.getEmail());
            shipmentRequest.setShippingAddress(account.getAddress());
            /*conversion del objeto a JSoon para envio - envio los bytes en un mensaje con body + parametros*/
            Message jsonMessage = MessageBuilder.withBody(objectMapper.writeValueAsString(shipmentRequest).getBytes())
                    .andProperties(MessagePropertiesBuilder.newInstance().setContentType("application/json")
                            .build()).build();
            /*enviar el mensaje - RabbitMq*/
            this.template.convertAndSend("INBOUND_SHIPMENT_ORDER", jsonMessage);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.debug(" [x] Sent '" + shipmentRequest.toString() + "'");
    }
}
