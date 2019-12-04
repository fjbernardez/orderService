package com.geekShirt.orderservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "Descripcion de los item a incluir en una orden")
public class LineItem {
    @ApiModelProperty(notes = "UPC (universal Product Code)",example = "1215454",required = true, position = 0)
    private String upc;
    @ApiModelProperty(notes = "quantity notes",example = "5",required = true, position = 1)
    private int quantity;
    @ApiModelProperty(notes = "price",example = "10.00",required = true, position = 2)
    private double price;
}
