package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDiscount {

    private Integer orderId;
    private Double discountTotal;
    private String itemList;

}
