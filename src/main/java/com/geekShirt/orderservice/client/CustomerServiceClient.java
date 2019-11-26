package com.geekShirt.orderservice.client;

import com.geekShirt.orderservice.config.OrderServiceConfig;
import com.geekShirt.orderservice.dto.AccountDto;
import com.geekShirt.orderservice.dto.AddressDto;
import com.geekShirt.orderservice.dto.CreditCardDto;
import com.geekShirt.orderservice.dto.CustomerDto;
import com.geekShirt.orderservice.util.AccountStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


/*@Slf4j propiedades de logeo de Lombok*/
@Slf4j
@Component
public class CustomerServiceClient {

    private RestTemplate restTemplate;

    @Autowired
    private OrderServiceConfig orderServiceConfig;

    //RestTemplateBuilder: Constructor de objetos RestTemplate
    public CustomerServiceClient(RestTemplateBuilder builder) {

        restTemplate = builder.build();
    }
    //metodo modificado con el eso de Option.class
    //basicamente estoy empaquetando la consulta para poder contemplar que este vacio, evitando el uso de null
    public Optional<AccountDto>  findAccountById (String accountId){
        /*restTemplate para realizar la llamda de un servicio a otro - retorna un .json que mapeo*/
        /*orderServiceConfig.getCustomerServiceUrl() = http://localhost:8089/api/v1/account*/
        Optional<AccountDto> optionalAccountDto = Optional.empty();
        try {
            optionalAccountDto = Optional.ofNullable(restTemplate.getForObject(
                    orderServiceConfig.getCustomerServiceUrl()+"/{id}",AccountDto.class,accountId));
            }
        catch (HttpClientErrorException ex){
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }

        }
        return optionalAccountDto;
    }

    /*cuenta para probar los servicios*/
    public AccountDto createDummyAccount() {

        /*declaraciones - Lombok*/

        AddressDto address = AddressDto.builder().street("nombreDeLaCalle").city("nombreDeLaCiudad").state("nombreDelEstado")
                                                .country("nombreDelPais").zipCode("zipCodeNumber").build();

        CustomerDto customer = CustomerDto.builder().lastName("lastNameCustomer").email("customerEmail").firstName("firstNameCustomer")
                                                    .build();

        CreditCardDto creditCard = CreditCardDto.builder().nameOnCard("cardName").number("cardNumber")
                                                        .type("cardType").ccv("ccvCard").build();

        /*finalmente creo account Dto*/
        AccountDto account = AccountDto.builder().address(address).customer(customer).creditCard(creditCard)
                                                .status(AccountStatus.ACTIVE).build();

        return account;
    }

   public AccountDto createAccount (AccountDto account) {
        AccountDto newAccount = restTemplate.postForObject(orderServiceConfig.getCustomerServiceUrl(),account,AccountDto.class);
        return newAccount;
   }

   public AccountDto createAccountBody (AccountDto account){
       ResponseEntity<AccountDto> responseAccount = restTemplate.postForEntity(orderServiceConfig.getCustomerServiceUrl()
                                                                                ,account,AccountDto.class);
       log.info("Response " + responseAccount.getHeaders());

       return responseAccount.getBody();
   }

   public void updateAccount (AccountDto account) {
        /*actualizacion*/
        restTemplate.put(orderServiceConfig.getCustomerServiceUrl()+"/{id}",account, account.getId());
    }

    public void deleteAccount(AccountDto account) {
        restTemplate.delete(orderServiceConfig.getCustomerServiceUrl()+"/{id}",account.getId() );

    }

}
