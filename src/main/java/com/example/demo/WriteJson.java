package com.example.demo;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderFullList;
import com.example.demo.domain.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteJson
{
    public static void main(String[] args) throws IOException {
        WriteJson test = new WriteJson();
        test.writeJson();
    }
    public void writeJson() throws IOException {
        OrderFullList orderFullList = new OrderFullList();
        List<Order> orderList = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderId(123456);
        List<OrderItem> toAdd = new ArrayList<>();
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setPrice(100d);
        orderItem1.setItemName("BOOK1");
        orderItem1.setItemType("BOOK");
        toAdd.add(orderItem1);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setPrice(200d);
        orderItem2.setItemName("CD1");
        orderItem2.setItemType("CD");
        toAdd.add(orderItem2);
        OrderItem orderItem3 = new OrderItem();
        orderItem3.setPrice(50d);
        orderItem3.setItemName("POSTCARD1");
        orderItem3.setItemType("POSTCARD");
        toAdd.add(orderItem3);
        order1.setOrderItems(toAdd);

        Order order2 = new Order();
        order2.setOrderId(567890);
        List<OrderItem> toAdd2 = new ArrayList<>();
        OrderItem orderItem4 = new OrderItem();
        orderItem4.setPrice(200d);
        orderItem4.setItemName("BOOK2");
        orderItem4.setItemType("BOOK");
        toAdd2.add(orderItem3);
        OrderItem orderItem5 = new OrderItem();
        orderItem5.setPrice(400d);
        orderItem5.setItemName("CD2");
        orderItem5.setItemType("CD");
        toAdd2.add(orderItem4);
        order2.setOrderItems(toAdd2);

        orderList.add(order1);
        orderList.add(order2);

        orderFullList.setOrderList(orderList);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonToString = objectMapper.writeValueAsString(orderFullList);
        System.out.println(jsonToString);
        File file = getInputFileObj();
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(jsonToString);
        fileWriter.flush();
        fileWriter.close();
    }

    public File getInputFileObj() {
        String path = "src/main/resources";
        File file = new File(path);
        return new File(file.getAbsolutePath() + "/file.txt");
    }
}
