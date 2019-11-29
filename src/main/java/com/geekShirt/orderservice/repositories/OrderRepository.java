package com.geekShirt.orderservice.repositories;

import com.geekShirt.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order,Long> {

    public List<Order> findOrdersByAccountId (String accountId);

    public Order findOrderByOrderId (String OrderId);

}