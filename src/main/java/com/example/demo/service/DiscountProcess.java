package com.example.demo.service;

import com.example.demo.domain.OrderItem;

public class DiscountProcess {

    private static final double BOOK_DISCOUNT = 0.95;
    private static final double CD_DISCOUNT = 0.9;
    private static final double POSTCARD_DISCOUNT = 0.85;

    public OrderItem processBook(OrderItem bookOrderItem){

        bookOrderItem.setPrice(bookOrderItem.getPrice()*BOOK_DISCOUNT);

        return bookOrderItem;
    }

    public  OrderItem processCD(OrderItem cdOrderItem){

        cdOrderItem.setPrice(cdOrderItem.getPrice()*CD_DISCOUNT);

        return cdOrderItem;
    }

    public  OrderItem processPostcard(OrderItem postcardOrderItem){

        postcardOrderItem.setPrice(postcardOrderItem.getPrice()*POSTCARD_DISCOUNT);

        return postcardOrderItem;
    }
}

