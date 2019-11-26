package com.geekShirt.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

//anotacion que indica que este objeto debe ir al contenedor de objetos. Se encargan de definir beans
@Configuration
@EnableSwagger2
/*esta anotación permite importar una clase/objeto del paquete pasado por parametro*/
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    // los Dockets ayudan a describir y documentar objetos. Bean indica que debe ser gestionado por el contenedor de Spring. FUNDAMENTAL
    @Bean
    public Docket apiDocket () {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build().apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo () {
        return new ApiInfo("Titulo","Descripcion del Servicio","VERSION (1.0)","Terminos de uso URL",
                new Contact("Nombre contacto","url de contacto","email de contacto")
                ,"Licencia","URL licencia", Collections.emptyList());
    }

}
