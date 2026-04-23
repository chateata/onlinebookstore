package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.LoginRegisterService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoginRegisterControllerUnitTest {

  private LoginRegisterController controller;

  @Mock private LoginRegisterService loginRegisterService;
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
    controller = new LoginRegisterController();
    setField(controller, "loginRegisterService", loginRegisterService);
  }

  @Test
  void userLoginHandler_shouldDelegate_andReturnSuccessRedirect() {
    User u = new User();
    ResultVO r = controller.userLoginHandler(u, session);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals("/", r.getData());
    verify(loginRegisterService).userLogin(eq(u), eq(session));
  }

  @Test
  void userRegisterHandler_shouldDelegate_andReturnSuccessRedirect() {
    User u = new User();
    ResultVO r = controller.userRegisterHandler(u);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals("/login", r.getData());
    verify(loginRegisterService).userRegister(eq(u));
  }

  @Test
  void adminLoginHandler_shouldDelegate_andReturnSuccessRedirect() {
    Admin a = new Admin();
    ResultVO r = controller.adminLoginHandler(a, session);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals("/admin/book_manage", r.getData());
    verify(loginRegisterService).adminLogin(eq(a), eq(session));
  }

  @Test
  void logoutHandlers_shouldRemoveSessionAttributes() {
    assertEquals("redirect:/login", controller.userLogout(session));
    verify(session).removeAttribute("user");

    assertEquals("redirect:/", controller.adminLogout(session));
    verify(session).removeAttribute("admin");
  }

  @Test
  void checkUserIsLoggedIn_whenMissing_shouldThrow() {
    when(session.getAttribute("user")).thenReturn(null);
    assertThrows(CustomizeException.class, () -> controller.checkUserIsLoggedIn(session));
  }

  @Test
  void checkUserIsLoggedIn_whenPresent_shouldReturnSuccess() {
    when(session.getAttribute("user")).thenReturn(new User());
    ResultVO r = controller.checkUserIsLoggedIn(session);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
  }
}

