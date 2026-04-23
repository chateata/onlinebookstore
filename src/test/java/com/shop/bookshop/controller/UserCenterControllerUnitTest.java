package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.OrderHandleService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserCenterControllerUnitTest {

  private UserCenterController controller;

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
    controller = new UserCenterController();
    setField(controller, "orderHandleService", orderHandleService);
  }

  @Test
  void getUserOrders_shouldDelegate_andReturnSuccess() {
    User u = new User();
    u.setUserId(7);
    when(session.getAttribute("user")).thenReturn(u);
    when(orderHandleService.getOrdersByUserId(eq(7), eq(1), eq(10)))
        .thenReturn(Collections.<Order>emptyList());

    ResultVO r = controller.getUserOrders(1, 10, session);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(0, r.getCount());
  }

  @Test
  void deleteOrder_shouldDelegate_andReturnSuccess() {
    ResultVO r = controller.deleteOrder(9);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(orderHandleService).deleteOrderById(9);
  }

  @Test
  void userCenter_shouldReturnTemplate() {
    assertEquals("user_center", controller.userCenter());
  }
}

