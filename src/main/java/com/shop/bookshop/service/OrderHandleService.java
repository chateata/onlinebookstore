package com.shop.bookshop.service;

import com.shop.bookshop.pojo.Order;

import java.util.List;

public interface OrderHandleService {

    /**
     * 创建订单
     * @param order
     */
    void createOrder(Order order);

    /**
     * 根据ID删除订单
     * @param orderId
     * @return
     */
    int deleteOrderById(Integer orderId);


    /**
     * 根据用户ID分页获取用户订单
     * @param userId
     * @return
     */
   List<Order> getOrdersByUserId(Integer userId,Integer page ,Integer limit);

    /**
     * 通过分页获取所有订单
     * @param page
     * @param limit
     * @return
     */
    List<Order> getAllOrdersByPage(Integer page, Integer limit);

    /**
     * 发货：按订单项进行分批发货并处理余额扣减与状态更新
     * @param orderId
     * @param shipItems 传入要发货的订单项（bookId/quantity）
     */
    void shipOrder(Integer orderId, List<com.shop.bookshop.pojo.OrderItem> shipItems);

}
