package com.shop.bookshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.shop.bookshop.dao.AdminMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminControllerUnitTest {

  private AdminController controller;

  @Mock private AdminMapper adminMapper;

  private static void setField(Object target, String name, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to set field: " + name, e);
    }
  }

  @Test
  void list_shouldReturnSuccessAndCount() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    Admin a = new Admin();
    a.setAdminName("a");
    Admin b = new Admin();
    b.setAdminName("b");
    List<Admin> list = Arrays.asList(a, b);
    when(adminMapper.selectAll()).thenReturn(list);

    ResultVO<List<Admin>> vo = controller.listAdmins();

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(2, vo.getCount());
    assertNotNull(vo.getData());
    assertEquals(2, vo.getData().size());
  }

  @Test
  void insert_whenNotExists_shouldSuccess() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    Admin input = new Admin();
    input.setAdminName("new");
    input.setPassword("secret123");
    when(adminMapper.selectByAdminName("new")).thenReturn(null);
    when(adminMapper.insert(any(Admin.class))).thenReturn(1);

    ResultVO<Void> vo = controller.insertAdmin(input);

    verify(adminMapper).insert(any(Admin.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void insert_whenExists_shouldThrow() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    Admin input = new Admin();
    input.setAdminName("exist");
    input.setPassword("secret123");
    when(adminMapper.selectByAdminName("exist")).thenReturn(new Admin());

    CustomizeException ex = assertThrows(CustomizeException.class, () -> controller.insertAdmin(input));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void update_whenNotExists_shouldThrow() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    Admin input = new Admin();
    input.setAdminName("missing");
    input.setPassword("p");
    when(adminMapper.selectByAdminName("missing")).thenReturn(null);

    CustomizeException ex = assertThrows(CustomizeException.class, () -> controller.updateAdmin(input));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void update_whenExists_shouldSuccess() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    Admin input = new Admin();
    input.setAdminName("exist");
    input.setPassword("p2");
    when(adminMapper.selectByAdminName("exist")).thenReturn(new Admin());
    when(adminMapper.updateByAdminName(any(Admin.class))).thenReturn(1);

    ResultVO<Void> vo = controller.updateAdmin(input);

    verify(adminMapper).updateByAdminName(any(Admin.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void delete_whenNotExists_shouldThrow() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    when(adminMapper.selectByAdminName("missing")).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> controller.deleteAdmin("missing"));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void delete_whenExists_shouldSuccess() {
    controller = new AdminController();
    setField(controller, "adminMapper", adminMapper);

    when(adminMapper.selectByAdminName("exist")).thenReturn(new Admin());
    when(adminMapper.deleteByAdminName("exist")).thenReturn(1);

    ResultVO<Void> vo = controller.deleteAdmin("exist");

    verify(adminMapper).deleteByAdminName("exist");
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }
}

