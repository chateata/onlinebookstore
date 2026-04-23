package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.OrderHandleService;
import com.shop.bookshop.service.OrderService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderControllerUnitTest {

  private OrderController controller;

  @Mock private OrderService orderService;
  @Mock private OrderHandleService orderHandleService;
  @Mock private HttpSession session;

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
    controller = new OrderController();
    setField(controller, "orderService", orderService);
    setField(controller, "orderHandleService", orderHandleService);
  }

  @Test
  void getOrderList_shouldReturnSuccess() {
    when(orderHandleService.getAllOrdersByPage(null, null)).thenReturn(Collections.emptyList());

    ResultVO r = controller.getOrderList(null, null);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(0, r.getCount());
  }

  @Test
  void orderSubmit_shouldSetUserIdAndDefaultPaymentStatus_andReturnRedirect() {
    User user = new User();
    user.setUserId(7);
    user.setUserName("u");
    when(session.getAttribute("user")).thenReturn(user);

    Order order = new Order();
    order.setPaymentStatus(null);

    ResultVO r = controller.orderSubmit(order, session);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals("/u/orders", r.getData());

    ArgumentCaptor<Order> cap = ArgumentCaptor.forClass(Order.class);
    verify(orderHandleService).createOrder(cap.capture());
    assertEquals(Integer.valueOf(7), cap.getValue().getUserId());
    assertEquals("PAID", cap.getValue().getPaymentStatus());
  }

  @Test
  void updateOrder_whenMissing_shouldReturnRecordNotFound() {
    when(orderService.selectByOrderId(1)).thenReturn(null);

    ResultVO r = controller.updateOrder(1, new Order());
    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r.getCode());
  }

  @Test
  void updateOrder_whenFound_shouldUpdateAndReturnSuccess() {
    when(orderService.selectByOrderId(1)).thenReturn(new Order());

    ResultVO r = controller.updateOrder(1, new Order());
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(orderService).updateByOrderId(any(Order.class));
  }

  @Test
  void deleteOrder_shouldReturnSuccess() {
    ResultVO r = controller.deleteOrder(1);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(orderService).deleteByOrderId(1);
  }

  @Test
  void getOrderByOrderId_foundAndMissing() {
    Order o = new Order();
    when(orderService.selectByOrderId(1)).thenReturn(o);
    ResultVO r1 = controller.getOrderByOrderId(1);
    assertEquals(ResultCode.SUCCESS.getCode(), r1.getCode());
    assertEquals(o, r1.getData());

    when(orderService.selectByOrderId(2)).thenReturn(null);
    ResultVO r2 = controller.getOrderByOrderId(2);
    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r2.getCode());
  }

  @Test
  void searchOrders_shouldNullOutEmptyConsigneeName_andReturnSuccess() {
    Order q = new Order();
    q.setConsigneeName("");
    when(orderService.searchOrders(eq(q), eq(1), eq(10))).thenReturn(Collections.emptyList());

    ResultVO r = controller.searchOrders(q, 1, 10);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertNotNull(r.getData());
  }
}

