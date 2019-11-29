package com.geekShirt.orderservice.entities;

import com.geekShirt.orderservice.util.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Table(name= "ORDERS")
@Entity
public class Order extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "ORDER_ID")
    private String orderId;
    @Column (name= "STATUS")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;
    @Column(name = "ACCOUND_ID")
    private String accountId;
    @Column (name = "TOTAL_AMOUNT")
    private double totalAmount;
    @Column (name = "TOTAL_TAX")
    private double totalTax;
    @Column (name = "TRANSACTION_DATE")
    private Date transactionDate;
    //genero relacion entre las entidades Order y OrderDetail. mappedBy fgenera una relacion bidireccional.
    //"order" debe ser una atributo valida en OrderDetail.class
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderDetail> details;
}
