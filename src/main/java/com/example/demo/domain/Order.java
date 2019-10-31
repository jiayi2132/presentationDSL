package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Order {

    private Integer orderId;
    private List<OrderItem> orderItems;

}
