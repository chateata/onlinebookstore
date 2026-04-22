package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.AdminMapper;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.util.ResultCode;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoginRegisterServiceImplUnitTest {

  private LoginRegisterServiceImpl service;

  @Mock private UserMapper userMapper;
  @Mock private AdminMapper adminMapper;
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
    service = new LoginRegisterServiceImpl();
    setField(service, "userMapper", userMapper);
    setField(service, "adminMapper", adminMapper);
  }

  @Test
  void userLogin_whenUserExistsAndPasswordMatches_shouldSetSessionUser() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("secret123");

    User dbUser = new User();
    dbUser.setUserId(1);
    dbUser.setUserName("alice");
    dbUser.setPassword("secret123");

    when(userMapper.selectByUserName("alice")).thenReturn(dbUser);

    service.userLogin(record, session);

    verify(session).setAttribute("user", dbUser);
  }

  @Test
  void userLogin_whenUserNotFound_shouldThrow() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("secret123");
    when(userMapper.selectByUserName("alice")).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.userLogin(record, session));
    assertEquals(ResultCode.USER_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  void userLogin_whenPasswordMismatch_shouldThrow() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("wrong");

    User dbUser = new User();
    dbUser.setUserName("alice");
    dbUser.setPassword("secret123");

    when(userMapper.selectByUserName("alice")).thenReturn(dbUser);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.userLogin(record, session));
    assertEquals(ResultCode.PASSWORD_ERROR.getCode(), ex.getCode());
  }

  @Test
  void userRegister_whenUserNotExists_shouldSetDefaultsAndInsert() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("secret123");
    when(userMapper.selectByUserName("alice")).thenReturn(null);

    service.userRegister(record);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userMapper).insert(captor.capture());
    User inserted = captor.getValue();
    assertEquals(Integer.valueOf(3), inserted.getCreditLevelId());
    assertNotNull(inserted.getAccountBalance());
    assertEquals(new BigDecimal("100.00"), inserted.getAccountBalance());
  }

  @Test
  void userRegister_whenUserNameExists_shouldThrow() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("secret123");
    when(userMapper.selectByUserName("alice")).thenReturn(new User());

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.userRegister(record));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void userRegister_whenDefaultsProvided_shouldNotOverride() {
    User record = new User();
    record.setUserName("alice");
    record.setPassword("secret123");
    record.setCreditLevelId(1);
    record.setAccountBalance(new BigDecimal("9.99"));
    when(userMapper.selectByUserName("alice")).thenReturn(null);

    service.userRegister(record);

    verify(userMapper).insert(any(User.class));
    assertEquals(Integer.valueOf(1), record.getCreditLevelId());
    assertEquals(new BigDecimal("9.99"), record.getAccountBalance());
  }

  @Test
  void adminLogin_whenAdminExistsAndPasswordMatches_shouldSetSessionAdmin() {
    Admin record = new Admin();
    record.setAdminName("admin");
    record.setPassword("p");

    Admin dbAdmin = new Admin();
    dbAdmin.setAdminName("admin");
    dbAdmin.setPassword("p");

    when(adminMapper.selectByAdminName("admin")).thenReturn(dbAdmin);

    service.adminLogin(record, session);

    verify(session).setAttribute("admin", dbAdmin);
  }

  @Test
  void adminLogin_whenAdminNotFound_shouldThrow() {
    Admin record = new Admin();
    record.setAdminName("admin");
    record.setPassword("p");
    when(adminMapper.selectByAdminName("admin")).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.adminLogin(record, session));
    assertEquals(ResultCode.USER_NOT_FOUND.getCode(), ex.getCode());
  }

  @Test
  void adminLogin_whenPasswordMismatch_shouldThrow() {
    Admin record = new Admin();
    record.setAdminName("admin");
    record.setPassword("wrong");

    Admin dbAdmin = new Admin();
    dbAdmin.setAdminName("admin");
    dbAdmin.setPassword("p");

    when(adminMapper.selectByAdminName("admin")).thenReturn(dbAdmin);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.adminLogin(record, session));
    assertEquals(ResultCode.PASSWORD_ERROR.getCode(), ex.getCode());
  }
}

