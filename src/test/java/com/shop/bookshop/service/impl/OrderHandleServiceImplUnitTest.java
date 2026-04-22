package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.CreditLevelMapper;
import com.shop.bookshop.dao.OrderItemMapper;
import com.shop.bookshop.dao.OrderMapper;
import com.shop.bookshop.dao.ShoppingCartMapper;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.CreditLevel;
import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.OrderItem;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.util.ResultCode;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OrderHandleServiceImplUnitTest {

  private OrderHandleServiceImpl service;

  @Mock private OrderMapper orderMapper;
  @Mock private OrderItemMapper orderItemMapper;
  @Mock private ShoppingCartMapper shoppingCartMapper;
  @Mock private BookMapper bookMapper;
  @Mock private UserMapper userMapper;
  @Mock private CreditLevelMapper creditLevelMapper;

  @BeforeEach
  void setUp() {
    service = new OrderHandleServiceImpl();
    ReflectionTestUtils.setField(service, "orderMapper", orderMapper);
    ReflectionTestUtils.setField(service, "orderItemMapper", orderItemMapper);
    ReflectionTestUtils.setField(service, "shoppingCartMapper", shoppingCartMapper);
    ReflectionTestUtils.setField(service, "bookMapper", bookMapper);
    ReflectionTestUtils.setField(service, "userMapper", userMapper);
    ReflectionTestUtils.setField(service, "creditLevelMapper", creditLevelMapper);
  }

  @Test
  void createOrder_shouldComputeDiscountedTotals_andPersistOrderAndItems() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(2);
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("1000.00"));

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(new BigDecimal("0.10")); // 10% off
    level.setOverdraftLimit(new BigDecimal("0.00"));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("100.00"));
    book.setStock(10);

    when(userMapper.selectByUserId(7)).thenReturn(user);
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);
    when(bookMapper.selectByBookId(99)).thenReturn(book);
    when(bookMapper.decrementStockIfEnough(99, 2)).thenReturn(1);
    when(orderMapper.insert(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order o = inv.getArgument(0);
              o.setOrderId(123);
              return 1;
            });

    service.createOrder(order);

    assertEquals(new BigDecimal("180.00"), order.getTotalAmount());
    assertNotNull(order.getPaymentStatus());
    assertNotNull(order.getShippingStatus());
    assertEquals(new BigDecimal("90.00"), item.getUnitPrice());
    assertEquals(new BigDecimal("90.00"), item.getPrice());
    assertEquals(new BigDecimal("180.00"), item.getSubtotal());
    assertEquals(Integer.valueOf(0), item.getShippedQuantity());

    verify(orderMapper).insert(order);
    verify(bookMapper).decrementStockIfEnough(99, 2);
    verify(orderItemMapper).insert(item);
    verify(shoppingCartMapper).deleteByUserIdAndBookId(eq(7), eq(99));
  }

  @Test
  void createOrder_whenNoItems_shouldThrow() {
    Order order = new Order();
    order.setOrderItems(Collections.emptyList());

    CustomizeException ex = assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenInsufficientStock_shouldThrow() {
    Order order = new Order();
    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(5);
    order.setOrderItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("100.00"));
    book.setStock(2);
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    CustomizeException ex = assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_STOCK.getCode(), ex.getCode());
  }
}

