package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.CategoryMapper;
import com.shop.bookshop.pojo.Category;
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
public class CategoryServiceImplUnitTest {

  private CategoryServiceImpl service;

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
    service = new CategoryServiceImpl();
    setField(service, "categoryMapper", categoryMapper);
  }

  @Test
  void selectByByCategoryCode_whenFound_shouldReturnCategory() {
    Category c = new Category();
    c.setCategoryCode("C1");
    c.setCategoryName("Cat");
    when(categoryMapper.selectByByCategoryCode("C1")).thenReturn(c);

    Category got = service.selectByByCategoryCode("C1");

    assertEquals("C1", got.getCategoryCode());
    verify(categoryMapper).selectByByCategoryCode("C1");
  }

  @Test
  void selectByByCategoryCode_whenNotFound_shouldReturnNull() {
    when(categoryMapper.selectByByCategoryCode("C999")).thenReturn(null);

    Category got = service.selectByByCategoryCode("C999");

    assertNull(got);
    verify(categoryMapper).selectByByCategoryCode("C999");
  }

  @Test
  void insert_shouldReturnMapperResult() {
    Category c = new Category();
    c.setCategoryCode("C1");
    c.setCategoryName("Cat");
    when(categoryMapper.insert(c)).thenReturn(1);

    int inserted = service.insert(c);

    assertEquals(1, inserted);
    verify(categoryMapper).insert(c);
  }

  @Test
  void updateByCategoryCode_shouldReturnMapperResult() {
    Category c = new Category();
    c.setCategoryCode("C1");
    c.setCategoryName("Cat2");
    when(categoryMapper.updateByCategoryCode(c)).thenReturn(1);

    int updated = service.updateByCategoryCode(c);

    assertEquals(1, updated);
    verify(categoryMapper).updateByCategoryCode(c);
  }

  @Test
  void deleteByByCategoryCode_shouldReturnMapperResult() {
    when(categoryMapper.deleteByByCategoryCode("C1")).thenReturn(1);
    when(categoryMapper.deleteByByCategoryCode("C999")).thenReturn(0);

    assertEquals(1, service.deleteByByCategoryCode("C1"));
    assertEquals(0, service.deleteByByCategoryCode("C999"));
  }

  @Test
  void selectAll_whenHasData_shouldReturnList() {
    List<Category> list = Arrays.asList(new Category(), new Category());
    when(categoryMapper.selectAll()).thenReturn(list);

    List<Category> got = service.selectAll(1, 10);

    assertEquals(2, got.size());
    verify(categoryMapper).selectAll();
  }

  @Test
  void selectAll_whenEmpty_shouldReturnEmptyList() {
    when(categoryMapper.selectAll()).thenReturn(Collections.emptyList());

    List<Category> got = service.selectAll(1, 10);

    assertEquals(0, got.size());
    verify(categoryMapper).selectAll();
  }
}

