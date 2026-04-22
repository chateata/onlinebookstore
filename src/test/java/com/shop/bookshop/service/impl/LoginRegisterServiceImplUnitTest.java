package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.util.ResultCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LoginRegisterServiceImplUnitTest {

  private LoginRegisterServiceImpl service;

  @Mock private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    service = new LoginRegisterServiceImpl();
    ReflectionTestUtils.setField(service, "userMapper", userMapper);
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
}

