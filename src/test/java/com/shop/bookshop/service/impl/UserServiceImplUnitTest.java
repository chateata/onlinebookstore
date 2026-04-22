package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.pojo.User;
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
public class UserServiceImplUnitTest {

  private UserServiceImpl service;

  @Mock private UserMapper userMapper;

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
    service = new UserServiceImpl();
    setField(service, "userMapper", userMapper);
  }

  @Test
  void deleteByUserId_shouldReturnMapperResult() {
    when(userMapper.deleteByUserId(1)).thenReturn(1);
    when(userMapper.deleteByUserId(999)).thenReturn(0);

    assertEquals(1, service.deleteByUserId(1));
    assertEquals(0, service.deleteByUserId(999));
  }

  @Test
  void insert_shouldReturnMapperResult() {
    User u = new User();
    u.setUserName("alice");
    when(userMapper.insert(u)).thenReturn(1);

    int inserted = service.insert(u);

    assertEquals(1, inserted);
    verify(userMapper).insert(u);
  }

  @Test
  void selectByUserId_whenFound_shouldReturnUser() {
    User u = new User();
    u.setUserId(1);
    when(userMapper.selectByUserId(1)).thenReturn(u);

    User got = service.selectByUserId(1);

    assertEquals(Integer.valueOf(1), got.getUserId());
    verify(userMapper).selectByUserId(1);
  }

  @Test
  void selectByUserId_whenNotFound_shouldReturnNull() {
    when(userMapper.selectByUserId(999)).thenReturn(null);

    User got = service.selectByUserId(999);

    assertNull(got);
    verify(userMapper).selectByUserId(999);
  }

  @Test
  void updateByUserId_shouldReturnMapperResult() {
    User u = new User();
    u.setUserId(1);
    when(userMapper.updateByUserId(u)).thenReturn(1);

    int updated = service.updateByUserId(u);

    assertEquals(1, updated);
    verify(userMapper).updateByUserId(u);
  }

  @Test
  void selectAll_whenHasData_shouldReturnList() {
    List<User> list = Arrays.asList(new User(), new User());
    when(userMapper.selectAll()).thenReturn(list);

    List<User> got = service.selectAll(1, 10);

    assertEquals(2, got.size());
    verify(userMapper).selectAll();
  }

  @Test
  void selectAll_whenEmpty_shouldReturnEmptyList() {
    when(userMapper.selectAll()).thenReturn(Collections.emptyList());

    List<User> got = service.selectAll(1, 10);

    assertEquals(0, got.size());
    verify(userMapper).selectAll();
  }

  @Test
  void searchUsers_shouldDelegateToMapper() {
    User criteria = new User();
    criteria.setUserName("a");
    when(userMapper.searchUsers(criteria)).thenReturn(Collections.singletonList(new User()));

    List<User> got = service.searchUsers(criteria, 1, 10);

    assertEquals(1, got.size());
    verify(userMapper).searchUsers(criteria);
  }
}

