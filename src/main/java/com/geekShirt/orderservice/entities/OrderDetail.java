package com.geekShirt.orderservice.entities;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ORDER_DETAILS")
@Entity
public class  OrderDetail extends CommonEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private double price;
    @Column(name = "TAX")
    private Double tax;
    @Column(name = "UPC")
    private  String upc;
    @ManyToOne (cascade = CascadeType.ALL) // estable la relacion con Order.class
    private Order order;
}
