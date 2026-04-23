package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderHandleServiceImplUnitTest {

  private OrderHandleServiceImpl service;

  @Mock private OrderMapper orderMapper;
  @Mock private OrderItemMapper orderItemMapper;
  @Mock private ShoppingCartMapper shoppingCartMapper;
  @Mock private BookMapper bookMapper;
  @Mock private UserMapper userMapper;
  @Mock private CreditLevelMapper creditLevelMapper;

  private static void setField(Object target, String name, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to set field: " + name, e);
    }
  }

  @BeforeEach
  void setUp() {
    service = new OrderHandleServiceImpl();
    setField(service, "orderMapper", orderMapper);
    setField(service, "orderItemMapper", orderItemMapper);
    setField(service, "shoppingCartMapper", shoppingCartMapper);
    setField(service, "bookMapper", bookMapper);
    setField(service, "userMapper", userMapper);
    setField(service, "creditLevelMapper", creditLevelMapper);
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
  void createOrder_whenOrderItemsNull_shouldThrow() {
    Order order = new Order();
    order.setUserId(7);
    order.setOrderItems(null);

    CustomizeException ex = assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenBookStockNull_shouldTreatAsZero_andThrowInsufficientStock() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("1000.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(BigDecimal.ZERO);
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("10.00"));
    book.setStock(null); // cover stock==null branch
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    CustomizeException ex = assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_STOCK.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenQuantityNull_shouldThrowFailed() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(null); // cover quantity==null branch
    order.setOrderItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("10.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("1000.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(BigDecimal.ZERO);
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    CustomizeException ex = assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenUserBalanceNull_shouldTreatAsZero_andAllowZeroTotal() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(null); // cover accountBalance==null branch
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(BigDecimal.ZERO);
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("0.00")); // make total 0 to avoid credit failure
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);
    when(bookMapper.decrementStockIfEnough(99, 1)).thenReturn(1);
    when(orderMapper.insert(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order o = inv.getArgument(0);
              o.setOrderId(123);
              return 1;
            });

    service.createOrder(order);

    assertEquals(new BigDecimal("0.00"), order.getTotalAmount());
    verify(shoppingCartMapper).deleteByUserIdAndBookId(eq(7), eq(99));
  }

  @Test
  void createOrder_whenUserIdNull_andTotalZero_shouldNotTouchShoppingCart() {
    Order order = new Order();
    order.setUserId(null); // cover userId==null branch at cart cleanup

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("0.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);
    when(bookMapper.decrementStockIfEnough(99, 1)).thenReturn(1);
    when(orderMapper.insert(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order o = inv.getArgument(0);
              o.setOrderId(123);
              return 1;
            });

    service.createOrder(order);

    verify(shoppingCartMapper, never()).deleteByUserIdAndBookId(any(Integer.class), any(Integer.class));
  }

  @Test
  void createOrder_whenNoItems_shouldThrow() {
    Order order = new Order();
    order.setOrderItems(Collections.emptyList());

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
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

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_STOCK.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenBookNotFound_shouldThrow() {
    Order order = new Order();
    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    when(bookMapper.selectByBookId(99)).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenQuantityInvalid_shouldThrow() {
    Order order = new Order();
    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(0);
    order.setOrderItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("1.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenConcurrentDecrementFails_shouldThrowInsufficientStock() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(2);
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setAccountBalance(new BigDecimal("1000.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("100.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);
    when(bookMapper.decrementStockIfEnough(99, 2)).thenReturn(0);
    when(orderMapper.insert(any(Order.class))).thenReturn(1);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_STOCK.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenInsufficientCredit_shouldThrow() {
    Order order = new Order();
    order.setUserId(7);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("0.00"));

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(new BigDecimal("0.00"));
    level.setOverdraftLimit(new BigDecimal("0.00"));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("1.00"));
    book.setStock(10);

    when(userMapper.selectByUserId(7)).thenReturn(user);
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_CREDIT.getCode(), ex.getCode());
  }

  @Test
  void createOrder_whenUserIdNull_shouldNotQueryUserOrCreditLevel_andShouldFailByCreditCheck() {
    Order order = new Order();
    order.setUserId(null);

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    order.setOrderItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("10.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.createOrder(order));
    assertEquals(ResultCode.INSUFFICIENT_CREDIT.getCode(), ex.getCode());
    verify(userMapper, never()).selectByUserId(any(Integer.class));
    verify(creditLevelMapper, never()).selectByLevelId(any(Integer.class));
  }

  @Test
  void createOrder_whenCreditLevelHasNullFields_shouldFallbackToZeroes() {
    Order order = new Order();
    order.setUserId(7);
    order.setPaymentStatus("CUSTOM"); // ensure defaults not applied
    order.setShippingStatus("CUSTOM");

    OrderItem item = new OrderItem();
    item.setBookId(99);
    item.setQuantity(1);
    item.setShippedQuantity(5); // ensure not overwritten to 0
    order.setOrderItems(Collections.singletonList(item));

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("1000.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setDiscountRate(null);
    level.setOverdraftLimit(null);
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    Book book = new Book();
    book.setBookId(99);
    book.setPrice(new BigDecimal("10.00"));
    book.setStock(10);
    when(bookMapper.selectByBookId(99)).thenReturn(book);
    when(bookMapper.decrementStockIfEnough(99, 1)).thenReturn(1);
    when(orderMapper.insert(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order o = inv.getArgument(0);
              o.setOrderId(1);
              return 1;
            });

    service.createOrder(order);

    assertEquals("CUSTOM", order.getPaymentStatus());
    assertEquals("CUSTOM", order.getShippingStatus());
    assertEquals(Integer.valueOf(5), item.getShippedQuantity());
    assertEquals(new BigDecimal("10.00"), item.getUnitPrice());
  }

  @Test
  void shipOrder_whenParamsInvalid_shouldThrow() {
    CustomizeException ex1 =
        assertThrows(CustomizeException.class, () -> service.shipOrder(null, Collections.emptyList()));
    assertEquals(ResultCode.FAILED.getCode(), ex1.getCode());

    CustomizeException ex2 =
        assertThrows(CustomizeException.class, () -> service.shipOrder(1, Collections.emptyList()));
    assertEquals(ResultCode.FAILED.getCode(), ex2.getCode());
  }

  @Test
  void shipOrder_whenOrderNotFound_shouldThrow() {
    when(orderMapper.selectByOrderId(999)).thenReturn(null);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(999, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void shipOrder_whenUnpaidAndEnoughBalance_shouldUpdateShippedAndStatusAndDeductBalance() {
    Order order = new Order();
    order.setOrderId(123);
    order.setUserId(7);
    order.setPaymentStatus("PENDING");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(5);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("100.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(2);

    service.shipOrder(123, Collections.singletonList(ship));

    assertEquals(Integer.valueOf(2), exist.getShippedQuantity());
    assertEquals("PARTIAL", order.getShippingStatus());
    assertEquals(new BigDecimal("80.00"), user.getAccountBalance());
    verify(userMapper).updateByUserId(user);
    verify(orderItemMapper).updateByOrderIdAndBookId(exist);
    verify(orderMapper).updateByOrderId(order);
  }

  @Test
  void shipOrder_whenPaid_shouldNotDeductBalance_andShouldUpdateStatus() {
    Order order = new Order();
    order.setOrderId(123);
    order.setUserId(7);
    order.setPaymentStatus("PAID");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(2);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    User user = new User();
    user.setUserId(7);
    user.setAccountBalance(new BigDecimal("1.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(2);

    service.shipOrder(123, Collections.singletonList(ship));

    assertEquals("SHIPPED", order.getShippingStatus());
    verify(userMapper, never()).updateByUserId(any(User.class));
    verify(orderItemMapper).updateByOrderIdAndBookId(exist);
    verify(orderMapper).updateByOrderId(order);
  }

  @Test
  void shipOrder_whenToShipExceedsRemaining_shouldThrow() {
    Order order = new Order();
    order.setOrderId(123);

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(2);
    exist.setShippedQuantity(1);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(2); // remain=1

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(123, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void shipOrder_whenShipItemNotInOrder_shouldThrow() {
    Order order = new Order();
    order.setOrderId(123);
    OrderItem exist = new OrderItem();
    exist.setBookId(1);
    exist.setQuantity(1);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));
    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(123, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void shipOrder_whenUnpaidAndInsufficientBalance_shouldThrow() {
    Order order = new Order();
    order.setOrderId(123);
    order.setUserId(7);
    order.setPaymentStatus("PENDING");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(5);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("0.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1); // shipTotal=10

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(123, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
    verify(userMapper, never()).updateByUserId(any(User.class));
  }

  @Test
  void shipOrder_whenToShipNonPositive_shouldSkipAndKeepPendingStatus() {
    Order order = new Order();
    order.setOrderId(123);
    order.setPaymentStatus("PAID");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(2);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(0); // should be skipped

    service.shipOrder(123, Collections.singletonList(ship));

    assertEquals(Integer.valueOf(0), exist.getShippedQuantity());
    assertEquals("PENDING", order.getShippingStatus());
    // implementation still persists shippedQuantity (0 delta) for each shipItem
    verify(orderItemMapper).updateByOrderIdAndBookId(exist);
    verify(orderMapper).updateByOrderId(order);
  }

  @Test
  void shipOrder_whenOrderItemsNull_shouldThrowOrderItemNotFound() {
    Order order = new Order();
    order.setOrderId(123);
    order.setOrderItems(null);
    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(123, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void shipOrder_whenUnitPriceNull_shouldFallbackToPrice() {
    Order order = new Order();
    order.setOrderId(123);
    order.setUserId(7);
    order.setPaymentStatus("PENDING");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(2);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(null);
    exist.setPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    User user = new User();
    user.setUserId(7);
    user.setCreditLevelId(3);
    user.setAccountBalance(new BigDecimal("100.00"));
    when(userMapper.selectByUserId(7)).thenReturn(user);

    CreditLevel level = new CreditLevel();
    level.setOverdraftLimit(new BigDecimal("0.00"));
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(level);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1); // shipTotal should use price 10.00

    service.shipOrder(123, Collections.singletonList(ship));

    assertEquals(new BigDecimal("90.00"), user.getAccountBalance());
    verify(userMapper).updateByUserId(user);
  }

  @Test
  void shipOrder_whenUnpaidAndUserNull_shouldFailByCreditCheck() {
    Order order = new Order();
    order.setOrderId(123);
    order.setUserId(7);
    order.setPaymentStatus("PENDING");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(1);
    exist.setShippedQuantity(0);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);
    when(userMapper.selectByUserId(7)).thenReturn(null);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.shipOrder(123, Collections.singletonList(ship)));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
    verify(userMapper, never()).updateByUserId(any(User.class));
    verify(orderItemMapper, never()).updateByOrderIdAndBookId(any(OrderItem.class));
    verify(orderMapper, never()).updateByOrderId(any(Order.class));
  }

  @Test
  void shipOrder_whenExistingShippedQuantityNull_shouldTreatAsZero() {
    Order order = new Order();
    order.setOrderId(123);
    order.setPaymentStatus("PAID");

    OrderItem exist = new OrderItem();
    exist.setBookId(99);
    exist.setQuantity(2);
    exist.setShippedQuantity(null);
    exist.setUnitPrice(new BigDecimal("10.00"));
    order.setOrderItems(Collections.singletonList(exist));

    when(orderMapper.selectByOrderId(123)).thenReturn(order);

    OrderItem ship = new OrderItem();
    ship.setBookId(99);
    ship.setQuantity(1);

    service.shipOrder(123, Collections.singletonList(ship));

    assertEquals(Integer.valueOf(1), exist.getShippedQuantity());
    assertEquals("PARTIAL", order.getShippingStatus());
  }
}

