package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.CreditLevelMapper;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.pojo.CreditLevel;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.UserService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

  private UserController controller;

  @Mock private UserService userService;
  @Mock private CreditLevelMapper creditLevelMapper;
  @Mock private UserMapper userMapper;
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
    controller = new UserController();
    setField(controller, "userService", userService);
    setField(controller, "creditLevelMapper", creditLevelMapper);
    setField(controller, "userMapper", userMapper);
  }

  @Test
  void adjustBalance_whenParamsMissing_shouldReturnFailed() {
    ResultVO<BigDecimal> vo1 = controller.adjustBalance(null, BigDecimal.ONE);
    assertEquals(ResultCode.FAILED.getCode(), vo1.getCode());

    ResultVO<BigDecimal> vo2 = controller.adjustBalance(1, null);
    assertEquals(ResultCode.FAILED.getCode(), vo2.getCode());
  }

  @Test
  void adjustBalance_whenUserNotFound_shouldReturnRecordNotFound() {
    when(userService.selectByUserId(1)).thenReturn(null);

    ResultVO<BigDecimal> vo = controller.adjustBalance(1, new BigDecimal("1.00"));

    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), vo.getCode());
  }

  @Test
  void adjustBalance_whenNextNegative_shouldReturnFailed_andNotUpdate() {
    User u = new User();
    u.setUserId(1);
    u.setAccountBalance(new BigDecimal("1.00"));
    when(userService.selectByUserId(1)).thenReturn(u);

    ResultVO<BigDecimal> vo = controller.adjustBalance(1, new BigDecimal("-2.00"));

    assertEquals(ResultCode.FAILED.getCode(), vo.getCode());
    verify(userService, never()).updateByUserId(any(User.class));
  }

  @Test
  void adjustBalance_whenOk_shouldUpdateAndReturnNext() {
    User u = new User();
    u.setUserId(1);
    u.setAccountBalance(new BigDecimal("1.00"));
    when(userService.selectByUserId(1)).thenReturn(u);

    ResultVO<BigDecimal> vo = controller.adjustBalance(1, new BigDecimal("2.50"));

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(new BigDecimal("3.50"), vo.getData());
    verify(userService).updateByUserId(u);
  }

  @Test
  void searchUsers_shouldNullOutEmptyFields_thenDelegate() {
    User criteria = new User();
    criteria.setUserName("");
    criteria.setEmail("");
    when(userService.searchUsers(eq(criteria), eq(1), eq(10))).thenReturn(new ArrayList<>());

    ResultVO<List<User>> vo = controller.searchUsers(criteria, 1, 10);

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(0, vo.getCount());
    assertEquals(null, criteria.getUserName());
    assertEquals(null, criteria.getEmail());
    verify(userService).searchUsers(criteria, 1, 10);
  }

  @Test
  void getUserCredit_whenNotLoggedIn_shouldReturnUserNotLoggedIn() {
    when(session.getAttribute("user")).thenReturn(null);

    ResultVO<Map<String, Object>> vo = controller.getUserCredit(session);

    assertEquals(ResultCode.USER_NOT_LOGGED_IN.getCode(), vo.getCode());
  }

  @Test
  void getUserCredit_whenFreshUserMissing_shouldReturnUserNotFound() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);
    when(userService.selectByUserId(1)).thenReturn(null);

    ResultVO<Map<String, Object>> vo = controller.getUserCredit(session);

    assertEquals(ResultCode.USER_NOT_FOUND.getCode(), vo.getCode());
  }

  @Test
  void getUserCredit_whenHasCreditLevel_shouldReturnCreditLevelData() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    User fresh = new User();
    fresh.setUserId(1);
    fresh.setAccountBalance(new BigDecimal("9.99"));
    fresh.setCreditLevelId(3);
    when(userService.selectByUserId(1)).thenReturn(fresh);

    CreditLevel cl = new CreditLevel();
    cl.setLevelId(3);
    when(creditLevelMapper.selectByLevelId(3)).thenReturn(cl);

    ResultVO<Map<String, Object>> vo = controller.getUserCredit(session);

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertNotNull(vo.getData());
    assertEquals(new BigDecimal("9.99"), vo.getData().get("accountBalance"));
    assertEquals(cl, vo.getData().get("creditLevel"));
  }

  @Test
  void getUserInfo_whenNotLoggedIn_shouldReturnUserNotLoggedIn() {
    when(session.getAttribute("user")).thenReturn(null);

    ResultVO<Map<String, Object>> vo = controller.getUserInfo(session);

    assertEquals(ResultCode.USER_NOT_LOGGED_IN.getCode(), vo.getCode());
  }

  @Test
  void getUserInfo_whenSuccess_shouldReturnBasicFields() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    User fresh = new User();
    fresh.setUserId(1);
    fresh.setUserName("alice");
    fresh.setEmail("a@b.com");
    when(userService.selectByUserId(1)).thenReturn(fresh);

    ResultVO<Map<String, Object>> vo = controller.getUserInfo(session);

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(1, ((Number) vo.getData().get("userId")).intValue());
    assertEquals("alice", vo.getData().get("userName"));
    assertEquals("a@b.com", vo.getData().get("email"));
  }

  @Test
  void changePassword_whenNotLoggedIn_shouldReturnUserNotLoggedIn() {
    when(session.getAttribute("user")).thenReturn(null);

    Map<String, String> payload = new HashMap<>();
    payload.put("oldPassword", "x");
    payload.put("newPassword", "123456");

    ResultVO<Void> vo = controller.changePassword(payload, session);

    assertEquals(ResultCode.USER_NOT_LOGGED_IN.getCode(), vo.getCode());
  }

  @Test
  void changePassword_whenMissingParams_shouldReturnFailed() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    ResultVO<Void> vo = controller.changePassword(new HashMap<>(), session);

    assertEquals(ResultCode.FAILED.getCode(), vo.getCode());
  }

  @Test
  void changePassword_whenNewPasswordTooShort_shouldReturnFailed() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    Map<String, String> payload = new HashMap<>();
    payload.put("oldPassword", "old");
    payload.put("newPassword", "12345");

    ResultVO<Void> vo = controller.changePassword(payload, session);

    assertEquals(ResultCode.FAILED.getCode(), vo.getCode());
  }

  @Test
  void changePassword_whenOldPasswordMismatch_shouldReturnFailed() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    User fresh = new User();
    fresh.setUserId(1);
    fresh.setPassword("old");
    when(userService.selectByUserId(1)).thenReturn(fresh);

    Map<String, String> payload = new HashMap<>();
    payload.put("oldPassword", "wrong");
    payload.put("newPassword", "123456");

    ResultVO<Void> vo = controller.changePassword(payload, session);

    assertEquals(ResultCode.FAILED.getCode(), vo.getCode());
    verify(userService, never()).updateByUserId(any(User.class));
  }

  @Test
  void changePassword_whenOk_shouldUpdatePassword() {
    User sessionUser = new User();
    sessionUser.setUserId(1);
    when(session.getAttribute("user")).thenReturn(sessionUser);

    User fresh = new User();
    fresh.setUserId(1);
    fresh.setPassword("old");
    when(userService.selectByUserId(1)).thenReturn(fresh);

    Map<String, String> payload = new HashMap<>();
    payload.put("oldPassword", "old");
    payload.put("newPassword", "123456");

    ResultVO<Void> vo = controller.changePassword(payload, session);

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals("123456", fresh.getPassword());
    verify(userService).updateByUserId(fresh);
  }
}

