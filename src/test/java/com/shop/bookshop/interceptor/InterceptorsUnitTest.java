package com.shop.bookshop.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.util.ResultCode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InterceptorsUnitTest {

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private HttpSession session;

  @BeforeEach
  void setUp() {
    when(request.getSession()).thenReturn(session);
  }

  @Test
  void clientLoginInterceptor_whenNoUser_shouldThrow() {
    when(session.getAttribute("user")).thenReturn(null);
    ClientLoginInterceptor interceptor = new ClientLoginInterceptor();
    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> interceptor.preHandle(request, response, new Object()));
    assertEquals(ResultCode.USER_NOT_LOGGED_IN.getCode(), ex.getCode());
  }

  @Test
  void clientLoginInterceptor_whenUserPresent_shouldAllow() throws Exception {
    when(session.getAttribute("user")).thenReturn(new User());
    ClientLoginInterceptor interceptor = new ClientLoginInterceptor();
    assertEquals(true, interceptor.preHandle(request, response, new Object()));
  }

  @Test
  void adminInterceptor_whenNoAdmin_shouldThrow() {
    when(session.getAttribute("admin")).thenReturn(null);
    AdminInterceptor interceptor = new AdminInterceptor();
    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> interceptor.preHandle(request, response, new Object()));
    assertEquals(ResultCode.USER_NOT_LOGGED_IN.getCode(), ex.getCode());
  }

  @Test
  void adminInterceptor_whenAdminPresent_shouldAllow() throws Exception {
    when(session.getAttribute("admin")).thenReturn(new Admin());
    AdminInterceptor interceptor = new AdminInterceptor();
    assertEquals(true, interceptor.preHandle(request, response, new Object()));
  }
}

