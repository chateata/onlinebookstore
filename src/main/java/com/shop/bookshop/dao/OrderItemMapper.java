package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.OrderItem;

import java.util.List;

public interface OrderItemMapper {

    int insert(OrderItem record);

    List<OrderItem> selectByOrderId(Integer orderId);

    int updateByOrderIdAndBookId(OrderItem record);
}