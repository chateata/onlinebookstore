package com.shop.bookshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.SupplierBookMapper;
import com.shop.bookshop.dao.SupplierMapper;
import com.shop.bookshop.pojo.Supplier;
import com.shop.bookshop.pojo.SupplierBook;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SupplierControllerUnitTest {

  private SupplierController controller;

  @Mock private SupplierMapper supplierMapper;
  @Mock private SupplierBookMapper supplierBookMapper;

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
    controller = new SupplierController();
    setField(controller, "supplierMapper", supplierMapper);
    setField(controller, "supplierBookMapper", supplierBookMapper);
  }

  @Test
  void listSuppliers_shouldReturnSuccessAndCount() {
    when(supplierMapper.selectAll()).thenReturn(Arrays.asList(new Supplier(), new Supplier()));

    ResultVO<List<Supplier>> vo = controller.listSuppliers();

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(2, vo.getCount());
    assertNotNull(vo.getData());
  }

  @Test
  void insertSupplier_shouldSuccess() {
    Supplier input = new Supplier();
    input.setName("S");
    when(supplierMapper.insert(any(Supplier.class))).thenReturn(1);

    ResultVO<Void> vo = controller.insertSupplier(input);

    verify(supplierMapper).insert(any(Supplier.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void updateSupplier_shouldSuccess() {
    Supplier input = new Supplier();
    input.setSupplierId(1);
    input.setName("S2");
    when(supplierMapper.updateBySupplierId(any(Supplier.class))).thenReturn(1);

    ResultVO<Void> vo = controller.updateSupplier(input);

    verify(supplierMapper).updateBySupplierId(any(Supplier.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void deleteSupplier_shouldSuccess() {
    when(supplierMapper.deleteBySupplierId(1)).thenReturn(1);

    ResultVO<Void> vo = controller.deleteSupplier(1);

    verify(supplierMapper).deleteBySupplierId(1);
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void listSupplierBooks_whenSupplierIdProvided_shouldFilter() {
    when(supplierBookMapper.selectBySupplierId(7))
        .thenReturn(Arrays.asList(new SupplierBook(), new SupplierBook()));

    ResultVO<List<SupplierBook>> vo = controller.listSupplierBooks(7);

    verify(supplierBookMapper).selectBySupplierId(7);
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(2, vo.getCount());
  }

  @Test
  void listSupplierBooks_whenNoSupplierId_shouldReturnAll() {
    when(supplierBookMapper.selectAll()).thenReturn(Collections.emptyList());

    ResultVO<List<SupplierBook>> vo = controller.listSupplierBooks(null);

    verify(supplierBookMapper).selectAll();
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(0, vo.getCount());
  }

  @Test
  void insertSupplierBook_shouldSetDefaultsAndSuccess() {
    SupplierBook input = new SupplierBook();
    input.setSupplierId(7);
    input.setTitle("T");
    input.setIsbn("I");
    // status/createTime/updateTime intentionally null to test defaults
    when(supplierBookMapper.insert(any(SupplierBook.class))).thenReturn(1);

    ResultVO<Void> vo = controller.insertSupplierBook(input);

    verify(supplierBookMapper).insert(any(SupplierBook.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals("ACTIVE", input.getStatus());
    assertNotNull(input.getCreateTime());
    assertNotNull(input.getUpdateTime());
  }

  @Test
  void updateSupplierBook_shouldSetUpdateTimeWhenMissing_andSuccess() {
    SupplierBook input = new SupplierBook();
    input.setSupplierBookId(9);
    input.setTitle("T2");
    input.setUpdateTime(null);
    when(supplierBookMapper.updateBySupplierBookId(any(SupplierBook.class))).thenReturn(1);

    ResultVO<Void> vo = controller.updateSupplierBook(input);

    verify(supplierBookMapper).updateBySupplierBookId(any(SupplierBook.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertNotNull(input.getUpdateTime());
  }

  @Test
  void deleteSupplierBook_shouldSuccess() {
    when(supplierBookMapper.deleteBySupplierBookId(9)).thenReturn(1);

    ResultVO<Void> vo = controller.deleteSupplierBook(9);

    verify(supplierBookMapper).deleteBySupplierBookId(9);
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void searchSupplierBooks_shouldPrioritizeIsbn_thenTitle_thenSupplierId_elseAll() {
    when(supplierBookMapper.selectByIsbn("I")).thenReturn(Arrays.asList(new SupplierBook()));
    when(supplierBookMapper.selectByTitle("T")).thenReturn(Arrays.asList(new SupplierBook(), new SupplierBook()));
    when(supplierBookMapper.selectBySupplierId(7)).thenReturn(Arrays.asList(new SupplierBook(), new SupplierBook(), new SupplierBook()));
    when(supplierBookMapper.selectAll()).thenReturn(Collections.emptyList());

    ResultVO<List<SupplierBook>> byIsbn = controller.searchSupplierBooks("I", "T", 7);
    assertEquals(1, byIsbn.getCount());

    ResultVO<List<SupplierBook>> byTitle = controller.searchSupplierBooks(null, "T", 7);
    assertEquals(2, byTitle.getCount());

    ResultVO<List<SupplierBook>> bySupplier = controller.searchSupplierBooks(null, null, 7);
    assertEquals(3, bySupplier.getCount());

    ResultVO<List<SupplierBook>> all = controller.searchSupplierBooks(null, null, null);
    assertEquals(0, all.getCount());
  }
}

