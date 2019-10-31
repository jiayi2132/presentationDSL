package com.example.demo.domain;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderItem {

    private String itemName;
    private String itemType;
    private Double price;

}
