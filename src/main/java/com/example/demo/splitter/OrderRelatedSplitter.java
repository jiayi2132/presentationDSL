package com.example.demo.splitter;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderFullList;
import com.example.demo.domain.OrderItem;

import java.util.List;

public class OrderRelatedSplitter {

    public List<Order> splitOrder(OrderFullList orderFullList) {
        return orderFullList.getOrderList();
    }

    public List<OrderItem> splitOrderItem(Order order) {
        return order.getOrderItems();
    }
}