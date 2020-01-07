package com.geekShirt.orderservice.service;

import com.geekShirt.orderservice.client.CustomerServiceClient;
import com.geekShirt.orderservice.client.InventoryServiceClient;
import com.geekShirt.orderservice.dto.AccountDto;
import com.geekShirt.orderservice.dto.OrderRequest;
import com.geekShirt.orderservice.entities.Order;
import com.geekShirt.orderservice.exception.AccountNotFoundExeption;
import com.geekShirt.orderservice.exception.IncorrectOrderRequestException;
import com.geekShirt.orderservice.exception.PaymentNotAcceptedException;
import com.geekShirt.orderservice.producer.ShippingOrderProducer;
import com.geekShirt.orderservice.repositories.OrderRepository;
import com.geekShirt.orderservice.util.ExeptionMessagesEnum;
import com.geekShirt.orderservice.util.OrderPaymentStatus;
import com.geekShirt.orderservice.util.OrderServiceDataTestUtils;
import com.geekShirt.orderservice.util.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

/*
Contiene todas las pruebas de OrderService
 */

/*Junit 5*/
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    /*definicion de objetos a ser administrados por Mockito*/
    @Mock
    private CustomerServiceClient customerServiceClient;
    @Mock
    private PaymentProcessorService paymentService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryServiceClient inventoryServiceClient;
    @Mock
    private ShippingOrderProducer shipmentMessageProducer;

    @BeforeEach
    public void init() {
        AccountDto mockAccount = OrderServiceDataTestUtils.getMockAccount("12345678");
        Mockito.doReturn(Optional.of(mockAccount)).when(customerServiceClient).findAccountById(anyString());
    }


    @DisplayName("should trow incorrect exception when Order Items are null")
    /*Esta anotacion permite que el test se comience a ejecutar*/
    /*para este test no es necesario ningun Mock. Es la Validacion del OrderRequest*/
    @Test
    public void shouldTrowIncorrectExceptionWhenOrderItemsAreNull(){
    /*se esta probando que en caso de no tener item se arroje la exception esperada*/
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAccountId("12345678");

        IncorrectOrderRequestException incorrectOrderRequestException = Assertions.assertThrows(
                        IncorrectOrderRequestException.class, ()->orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExeptionMessagesEnum.INCORRECT_REQUEST_EMTY_ITEMS_ORDER.getValue(),incorrectOrderRequestException.getMessage());

    }

    @DisplayName("should trow incorrect exception when Order Items are empty")
    /*Esta anotacion permite que el test se comience a ejecutar*/
    @Test
    public void shouldTrowIncorrectExceptionWhenOrderItemsAreEmpty(){
        /*se esta probando que en caso de no tener item se arroje la exception esperada*/
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAccountId("12345678");
        orderRequest.setItems(new ArrayList<>());

        IncorrectOrderRequestException incorrectOrderRequestException = Assertions.assertThrows(
                IncorrectOrderRequestException.class, ()->orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExeptionMessagesEnum.INCORRECT_REQUEST_EMTY_ITEMS_ORDER.getValue(),incorrectOrderRequestException.getMessage());

    }

    /*para el siguiente test se necesita del servicio externo customerServiceClient. Esta es la razón por la que se utiiza el frameWork Mokito*/
    @DisplayName("Should Throw Account Not Found Exception When Account Does Not Exists")
    @Test
    public void shouldThrowAccountNotFoundWhenAccountDoesNotExists() {

        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        /*cuando se llame a customerServiceCliente.findAccountById con cualquier parametro, retorna un objeto vacio.
        * Para este test en particular se describe que comportamiento es el que debe simular pasado este metodo
        * */
        Mockito.when(customerServiceClient.findAccountById(anyString())).thenReturn(Optional.empty());

        AccountNotFoundExeption accountNotFoundException = Assertions.assertThrows(AccountNotFoundExeption.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExeptionMessagesEnum.ACCOUNT_NOT_FOUND.getValue(), accountNotFoundException.getMessage());
        /*Se verifica que durante la ejecucuion del test se ejecute el metodo en cuestion.
        Es util solo con conocimiento del denominado "happy path" de la aplicacion, de manera de poder tener esa verificacion.
        Por defecto la cantidad de ejecuciones es 1*/
        Mockito.verify(customerServiceClient).findAccountById(anyString());
    }

    @DisplayName("Should Throw Payment Not Accepted Exception When Payment is Denied")
    @Test
    public void shouldThrowPaymentNotAcceptedExceptionWhenPaymentIsDenied() {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        AccountDto mockAccount = OrderServiceDataTestUtils.getMockAccount(orderRequest.getAccountId());

        Mockito.doReturn(Optional.of(mockAccount)).when(customerServiceClient).findAccountById(anyString());

        Mockito.when(paymentService.processPayment(any(), any())).thenReturn(OrderServiceDataTestUtils
                .getMockPayment(orderRequest.getAccountId(), OrderPaymentStatus.DENIED));

        Mockito.doReturn(new Order()).when(orderRepository).save(any(Order.class));

        PaymentNotAcceptedException paymentNotAcceptedException = Assertions.assertThrows(PaymentNotAcceptedException.class,
                () -> orderService.createOrder(orderRequest));

        Assertions.assertEquals(ExeptionMessagesEnum.PAYMENT_ADDED_NOT_ACCEPTED.getValue(), paymentNotAcceptedException.getMessage());
        Mockito.verify(customerServiceClient).findAccountById(anyString());
        Mockito.verify(orderRepository).save(any(Order.class));
        Mockito.verify(paymentService).processPayment(any(), any());
    }

    @DisplayName("Should Return Pending Order When Create Order is Called")
    @Test
    public void shouldReturnPendingOrderWhenCreateOrderIsCalled() throws PaymentNotAcceptedException {
        OrderRequest orderRequest = OrderServiceDataTestUtils.getMockOrderRequest("12345678");

        Mockito.when(paymentService.processPayment(any(), any())).thenReturn(OrderServiceDataTestUtils
                .getMockPayment(orderRequest.getAccountId(), OrderPaymentStatus.APPROVED));

        Mockito.doNothing().when(inventoryServiceClient).updateInventory(anyList());
        Mockito.doNothing().when(shipmentMessageProducer).send(anyString(), any());
        Mockito.when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order order = orderService.createOrder(orderRequest);

        Assertions.assertEquals("12345678", order.getAccountId());
        Assertions.assertEquals(Double.valueOf("101.0"), order.getTotalAmount());
        Assertions.assertEquals(Double.valueOf("16.16"), order.getTotalTax());
        Assertions.assertEquals(Double.valueOf("117.16"), order.getTotalAmountTax());
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
        Assertions.assertEquals(2, order.getDetails().size());
        Assertions.assertEquals(OrderPaymentStatus.APPROVED, order.getPaymentStatus());
        Assertions.assertNotNull(order.getTransactionDate());

     /*
        assertThat(order.getOrderId(), not(isEmptyString()));
        assertThat(order.getAccountId(), is(Matchers.equalTo("12345678")));
        assertThat(order.getTotalAmount(), is(Matchers.equalTo(1005d)));
        assertThat(order.getTotalTax(), is(Matchers.equalTo(160.8d)));
        assertThat(order.getTotalAmountTax(), is(Matchers.equalTo(1165.8d)));
        assertThat(order.getStatus(), is(Matchers.equalTo(OrderStatus.PENDING)));
        assertThat(order.getDetails().size(), is(Matchers.equalTo(2)));
        assertThat(order.getPaymentStatus(), is(Matchers.equalTo(OrderPaymentStatus.APPROVED)));
        assertThat(order.getTransactionDate(), is(Matchers.notNullValue()));
     */

        Mockito.verify(customerServiceClient).findAccountById(anyString());
        Mockito.verify(paymentService).processPayment(any(), any());
        Mockito.verify(inventoryServiceClient).updateInventory(anyList());
        Mockito.verify(shipmentMessageProducer).send(anyString(), any());
        Mockito.verify(orderRepository).save(any(Order.class));
    }
}
