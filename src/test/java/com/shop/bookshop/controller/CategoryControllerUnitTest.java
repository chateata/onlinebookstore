package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.CategoryMapper;
import com.shop.bookshop.pojo.Category;
import com.shop.bookshop.service.CategoryService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerUnitTest {

  private CategoryController controller;

  @Mock private CategoryService categoryService;
  @Mock private CategoryMapper categoryMapper;

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
    controller = new CategoryController();
    setField(controller, "categoryService", categoryService);
    setField(controller, "categoryMapper", categoryMapper);
  }

  @Test
  void categorySearchByCode_foundAndMissing() {
    Category c = new Category();
    c.setCategoryCode("C1");
    when(categoryService.selectByByCategoryCode("C1")).thenReturn(c);
    ResultVO r1 = controller.categorySearchByCode("C1");
    assertEquals(ResultCode.SUCCESS.getCode(), r1.getCode());

    when(categoryService.selectByByCategoryCode("C2")).thenReturn(null);
    ResultVO r2 = controller.categorySearchByCode("C2");
    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r2.getCode());
  }

  @Test
  void categoryInsertUpdateDelete_shouldReturnSuccess() {
    assertEquals(ResultCode.SUCCESS.getCode(), controller.categoryDelete("C1").getCode());
    assertEquals(ResultCode.SUCCESS.getCode(), controller.categoryInsert(new Category()).getCode());
    assertEquals(ResultCode.SUCCESS.getCode(), controller.categoryUpdate(new Category()).getCode());
  }

  @Test
  void categorySearchAll_shouldDefaultPageLimit_andReturnRecordNotFoundWhenEmpty() {
    when(categoryService.selectAll(eq(0), eq(10))).thenReturn(Collections.emptyList());
    ResultVO r1 = controller.categorySearchAll(null, null);
    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r1.getCode());

    when(categoryService.selectAll(eq(2), eq(5))).thenReturn(Collections.singletonList(new Category()));
    ResultVO r2 = controller.categorySearchAll(2, 5);
    assertEquals(ResultCode.SUCCESS.getCode(), r2.getCode());
    verify(categoryService).selectAll(2, 5);
  }
}

