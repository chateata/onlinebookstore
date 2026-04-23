package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.PurchaseOrderItemMapper;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.pojo.PurchaseOrderItem;
import com.shop.bookshop.service.PurchaseService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
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
public class PurchaseControllerUnitTest {

  private PurchaseController controller;

  @Mock private PurchaseService purchaseService;
  @Mock private PurchaseOrderMapper purchaseOrderMapper;
  @Mock private PurchaseOrderItemMapper purchaseOrderItemMapper;

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
    controller = new PurchaseController();
    setField(controller, "purchaseService", purchaseService);
    setField(controller, "purchaseOrderMapper", purchaseOrderMapper);
    setField(controller, "purchaseOrderItemMapper", purchaseOrderItemMapper);
  }

  @Test
  void list_whenPoIdProvided_foundAndMissing() {
    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(1);
    when(purchaseOrderMapper.selectByPoId(1)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(1)).thenReturn(Collections.emptyList());

    ResultVO<Object> r1 = controller.list(1);
    assertEquals(ResultCode.SUCCESS.getCode(), r1.getCode());
    assertEquals(1, r1.getCount());

    when(purchaseOrderMapper.selectByPoId(2)).thenReturn(null);
    ResultVO<Object> r2 = controller.list(2);
    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r2.getCode());
  }

  @Test
  void list_whenPoIdNull_shouldReturnAll_andHandleNullList() {
    List<PurchaseOrder> list = Arrays.asList(new PurchaseOrder(), new PurchaseOrder());
    when(purchaseOrderMapper.selectAll()).thenReturn(list);

    ResultVO<Object> r1 = controller.list(null);
    assertEquals(ResultCode.SUCCESS.getCode(), r1.getCode());
    assertEquals(2, r1.getCount());

    when(purchaseOrderMapper.selectAll()).thenReturn(null);
    ResultVO<Object> r2 = controller.list(null);
    assertEquals(ResultCode.SUCCESS.getCode(), r2.getCode());
    assertEquals(0, r2.getCount());
  }

  @Test
  void create_shouldDelegateToService_andReturnPoId() {
    when(purchaseService.createPurchaseOrder(any(PurchaseOrder.class))).thenReturn(9);
    ResultVO<Integer> r = controller.create(new PurchaseOrder());
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(Integer.valueOf(9), r.getData());
  }

  @Test
  void receive_whenPoItemIdNull_shouldReturnError() {
    ResultVO<Void> r = controller.receive(null, 1);
    assertEquals(ResultCode.FAILED.getCode(), r.getCode());
    verify(purchaseService, never()).receivePurchaseOrderItem(any(Integer.class), any(Integer.class));
  }

  @Test
  void receive_whenAddedReceivedNull_shouldComputeRemaining_andDelegate() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setQuantity(10);
    poi.setReceivedQuantity(3);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    ResultVO<Void> r = controller.receive(11, null);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(purchaseService).receivePurchaseOrderItem(11, 7);
  }

  @Test
  void receive_whenAddedReceivedNull_butPoiMissing_shouldReturnError() {
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(null);

    ResultVO<Void> r = controller.receive(11, null);
    assertEquals(ResultCode.FAILED.getCode(), r.getCode());
    verify(purchaseService, never()).receivePurchaseOrderItem(any(Integer.class), any(Integer.class));
  }
}

