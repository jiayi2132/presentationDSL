package com.example.demo.aggregator;

import com.example.demo.domain.OrderDiscount;
import com.example.demo.domain.OrderItem;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAggregator {
    public OrderDiscount aggregateOrderItem(List<Message<OrderItem>> results) {
        List<String> itemList = new ArrayList<>();
        OrderDiscount toReturn = new OrderDiscount();
        double discountTotal = 0;
        if (!results.isEmpty()) {
            for (Message<OrderItem> partialResult : results) {
                OrderItem thisOrderItem = partialResult.getPayload();
                discountTotal += thisOrderItem.getPrice();
                itemList.add(thisOrderItem.getItemName());
            }
        }

        toReturn.setOrderId((Integer) results.get(0).getHeaders().get("orderId"));
        toReturn.setItemList(itemList.toString());
        toReturn.setDiscountTotal(discountTotal);

        return toReturn;
    }
}
