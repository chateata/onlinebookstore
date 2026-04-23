package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.OrderMapper;
import com.shop.bookshop.pojo.Order;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplUnitTest {

  private OrderServiceImpl service;

  @Mock private OrderMapper orderMapper;

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
    service = new OrderServiceImpl();
    setField(service, "orderMapper", orderMapper);
  }

  @Test
  void deleteByOrderId_shouldCallMapper_evenThoughReturnValueIsZeroInImplementation() {
    when(orderMapper.deleteByOrderId(1)).thenReturn(1);

    int result = service.deleteByOrderId(1);

    assertEquals(0, result);
    verify(orderMapper).deleteByOrderId(1);
  }

  @Test
  void insert_isDeprecatedAndReturnsZero() {
    int result = service.insert(new Order());

    assertEquals(0, result);
  }

  @Test
  void selectByOrderId_whenFound_shouldReturnOrder() {
    Order o = new Order();
    o.setOrderId(1);
    when(orderMapper.selectByOrderId(1)).thenReturn(o);

    Order got = service.selectByOrderId(1);

    assertEquals(Integer.valueOf(1), got.getOrderId());
    verify(orderMapper).selectByOrderId(1);
  }

  @Test
  void selectByOrderId_whenMissing_shouldReturnNull() {
    when(orderMapper.selectByOrderId(999)).thenReturn(null);

    Order got = service.selectByOrderId(999);

    assertNull(got);
    verify(orderMapper).selectByOrderId(999);
  }

  @Test
  void updateByOrderId_shouldReturnMapperResult() {
    Order o = new Order();
    o.setOrderId(1);
    when(orderMapper.updateByOrderId(o)).thenReturn(1);

    int updated = service.updateByOrderId(o);

    assertEquals(1, updated);
    verify(orderMapper).updateByOrderId(o);
  }

  @Test
  void selectAll_shouldReturnList() {
    List<Order> list = Arrays.asList(new Order(), new Order());
    when(orderMapper.selectAll()).thenReturn(list);

    List<Order> got = service.selectAll();

    assertEquals(2, got.size());
    verify(orderMapper).selectAll();
  }

  @Test
  void selectByUserId_shouldReturnList() {
    when(orderMapper.selectByUserId(7)).thenReturn(Collections.singletonList(new Order()));

    List<Order> got = service.selectByUserId(7);

    assertEquals(1, got.size());
    verify(orderMapper).selectByUserId(7);
  }

  @Test
  void searchOrders_shouldDelegateToMapper() {
    Order criteria = new Order();
    when(orderMapper.searchOrders(criteria)).thenReturn(Collections.singletonList(new Order()));

    List<Order> got = service.searchOrders(criteria, 1, 10);

    assertEquals(1, got.size());
    verify(orderMapper).searchOrders(criteria);
  }
}

