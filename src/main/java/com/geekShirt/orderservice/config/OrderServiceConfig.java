package com.geekShirt.orderservice.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.amqp.core.Queue;


/*Getter necesario para poder acceder a la clase*/
@Getter
/*habilita que se auditen con springData*/
@EnableJpaAuditing
@Configuration
/*anotacion que vincula la clase OrderServiceConfig con el archivo pasado en "classpath" */
@PropertySource({"classpath:application.properties"})
public class OrderServiceConfig {
    /*Anotacion que realiza el mapeo de la propiedad en el archivo de configuracion*/
    @Value("${customerservice.url}")
    String customerServiceUrl ;

    @Value("${paymentservice.url}")
    String paymentServiceUrl ;

    @Value("${inventoryservice.url}")
    String inventoryServiceUrl ;

    @Qualifier(value = "outbound")
    @Bean
    public Queue inboundShipmentOrder() {
        return new Queue
                ("INBOUND_SHIPMENT_ORDER", false, false, false);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }
}
