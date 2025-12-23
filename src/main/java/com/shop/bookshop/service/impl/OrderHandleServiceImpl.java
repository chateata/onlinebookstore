package com.shop.bookshop.service.impl;

import com.github.pagehelper.PageHelper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.OrderItemMapper;
import com.shop.bookshop.dao.OrderMapper;
import com.shop.bookshop.dao.ShoppingCartMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.OrderItem;
import com.shop.bookshop.service.OrderHandleService;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderHandleServiceImpl implements OrderHandleService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private BookMapper bookMapper;

    /**
     * 创建用户订单
     * @param order
     */
    @Override
    public void createOrder(Order order) {

        //添加父订单
        orderMapper.insert(order);
        //添加订单子项
        for (OrderItem orderItem :order.getOrderItems()) {
            orderItem.setOrderId(order.getOrderId());
            orderItemMapper.insert(orderItem);
            //如果存在购物车，则删除购物车
            shoppingCartMapper.deleteByUserIdAndBookId(order.getUserId(), orderItem.getBookId());
            //判断书籍库存
            Book book = bookMapper.selectByBookId(orderItem.getBookId());
            if(book.getStock()<orderItem.getQuantity()){
                //库存不足
                throw new CustomizeException(ResultCode.FAILED,"书籍《"+book.getBookName()+"》的库存不足");
            }
            //修改书籍库存
            book.setStock(book.getStock()-orderItem.getQuantity());
            bookMapper.updateByBookId(book);
        }
    }

    @Override
    public int deleteOrderById(Integer orderId) {
        orderMapper.deleteByOrderId(orderId);
        return 0;
    }

    /**
     * 查询用户订单
     * @param userId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<Order> getOrdersByUserId(Integer userId, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Order> orders = orderMapper.selectByUserId(userId);
        return orders;
    }

    /**
     * 查询全部订单
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<Order> getAllOrdersByPage(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Order> orders = orderMapper.selectAll();
        return orders;
    }
}
