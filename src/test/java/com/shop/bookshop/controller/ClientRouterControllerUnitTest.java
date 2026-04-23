package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.shop.bookshop.pojo.User;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientRouterControllerUnitTest {

  private ClientRouterController controller;

  @Mock private HttpSession session;

  @BeforeEach
  void setUp() {
    controller = new ClientRouterController();
  }

  @Test
  void toHomePage_shouldReturnIndex() {
    assertEquals("index", controller.toHomePage());
  }

  @Test
  void toLogin_whenAlreadyLoggedIn_shouldRedirectHome() {
    when(session.getAttribute("user")).thenReturn(new User());
    assertEquals("redirect:/", controller.toLogin(session));
  }

  @Test
  void toLogin_whenNotLoggedIn_shouldReturnLogin() {
    when(session.getAttribute("user")).thenReturn(null);
    assertEquals("login", controller.toLogin(session));
  }

  @Test
  void toRegister_shouldReturnRegister() {
    assertEquals("register", controller.toRegister());
  }

  @Test
  void toOrderCenter_andShoppingCart_shouldCheckUserName() {
    User u = new User();
    u.setUserName("u");
    when(session.getAttribute("user")).thenReturn(u);

    assertEquals("user_orders", controller.toOrderCenter("u", session));
    assertEquals("shopping_cart", controller.toUserShoppingCart("u", session));

    assertEquals("redirect:/login", controller.toOrderCenter("x", session));
    assertEquals("redirect:/login", controller.toUserShoppingCart("x", session));
  }
}

