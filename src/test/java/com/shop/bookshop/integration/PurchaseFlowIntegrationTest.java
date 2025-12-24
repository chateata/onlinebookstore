package com.shop.bookshop.integration;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.OrderItem;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.OrderHandleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.Collections;

@SpringBootTest
@Transactional
@Rollback
public class PurchaseFlowIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private OrderHandleService orderHandleService;

    @Test
    public void testCreateOrderAndBalanceAndStockChange() {
        // create user with 100 balance
        User user = new User();
        user.setUserName("test_user_it");
        user.setPassword("password");
        user.setEmail("it@example.com");
        user.setAccountBalance(new BigDecimal("100.00"));
        userMapper.insert(user);
        Integer userId = user.getUserId();
        Assertions.assertNotNull(userId);

        // create book with price 10.00 and stock 10
        Book book = new Book();
        book.setBookName("IT Test Book");
        book.setIsbn("IT-ISBN-0001");
        book.setPrice(new BigDecimal("10.00"));
        book.setStock(10);
        bookMapper.insert(book);
        Integer bookId = book.getBookId();
        Assertions.assertNotNull(bookId);

        // create order with 2 copies, mark as PAID to trigger balance deduction
        Order order = new Order();
        order.setUserId(userId);
        order.setPaymentStatus("PAID");
        OrderItem oi = new OrderItem();
        oi.setBookId(bookId);
        oi.setQuantity(2);
        order.setOrderItems(Collections.singletonList(oi));

        orderHandleService.createOrder(order);

        // verify stock reduced by 2
        Book after = bookMapper.selectByBookId(bookId);
        Assertions.assertEquals(8, after.getStock().intValue());

        // verify balance reduced by 20.00
        User afterUser = userMapper.selectByUserId(userId);
        Assertions.assertEquals(new BigDecimal("80.00"), afterUser.getAccountBalance());
    }
}


