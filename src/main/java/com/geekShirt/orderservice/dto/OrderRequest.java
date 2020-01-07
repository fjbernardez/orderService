package com.geekShirt.orderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "Esta clase representa una orden a ser procesada")
public class OrderRequest {
    @NotNull
    @NotBlank
    @ApiModelProperty(notes = "Account Id", example = "98765432112234", required = true)
    private String accountId;

    @ApiModelProperty(notes = "Items incluidos en la orden", required = true)
    private List<LineItem> items;
}
/*
* swagger detecta atributos del validation framework esta basada JSR203
**/