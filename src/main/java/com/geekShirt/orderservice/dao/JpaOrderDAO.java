package com.geekShirt.orderservice.dao;

import com.geekShirt.orderservice.entities.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/*
* @Repository indica a Spring que gestione un EntityManagerFactory y cree un EntityManager
* */
@Repository
public class JpaOrderDAO implements OrderDAO {

    /*Por medio de @PersistenceContext tengo referencia del EntityManager*/
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class).getResultList();
    }

    @Override
    public Optional<Order> findByOrderId(String orderId) {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.orderId = :orderId",Order.class);
        query.setParameter("orderId", orderId);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public Optional<Order> findById(Long id) {
        /*
        * JPA incluye un metodo de busqueda directamente por id, por lo que no es necesaria toda esta estructura.
        *  TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o WHERE o.id = : id",Order.class);
        *  query.setParameter("id",id);
        *  return Optional.ofNullable(query.getSingleResult());
        * */
        return Optional.ofNullable(em.find(Order.class, id));
    }

    @Override
    public Order save(Order order) {
        /*
        * Persisto el objeto y lo retorno solo para tener un metodo de verificacion.
        * */
        em.persist(order);
        return order;
    }
}
