package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.PurchaseOrderItemMapper;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.pojo.PurchaseOrderItem;
import com.shop.bookshop.util.ResultCode;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceImplUnitTest {

  private PurchaseServiceImpl service;

  @Mock private PurchaseOrderMapper purchaseOrderMapper;
  @Mock private PurchaseOrderItemMapper purchaseOrderItemMapper;
  @Mock private BookMapper bookMapper;

  @BeforeEach
  void setUp() {
    service = new PurchaseServiceImpl();
    ReflectionTestUtils.setField(service, "purchaseOrderMapper", purchaseOrderMapper);
    ReflectionTestUtils.setField(service, "purchaseOrderItemMapper", purchaseOrderItemMapper);
    ReflectionTestUtils.setField(service, "bookMapper", bookMapper);
  }

  @Test
  void receivePurchaseOrderItem_shouldCapToRemaining_updateReceivedAndOrderStatus() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setQuantity(10);
    poi.setReceivedQuantity(3);

    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    Book book = new Book();
    book.setBookId(33);
    book.setStock(5);
    when(bookMapper.selectByBookId(33)).thenReturn(book);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(null);
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);

    PurchaseOrderItem refreshed = new PurchaseOrderItem();
    refreshed.setQuantity(10);
    refreshed.setReceivedQuantity(7);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(refreshed));

    service.receivePurchaseOrderItem(11, 4);

    assertEquals(Integer.valueOf(7), poi.getReceivedQuantity());
    assertEquals(Integer.valueOf(9), book.getStock());
    verify(purchaseOrderItemMapper).updateByPoItemId(poi);
    verify(bookMapper).updateByBookId(book);

    ArgumentCaptor<PurchaseOrder> poCaptor = ArgumentCaptor.forClass(PurchaseOrder.class);
    verify(purchaseOrderMapper).updateByPoId(poCaptor.capture());
    PurchaseOrder updated = poCaptor.getValue();
    assertEquals("PARTIAL", updated.getStatus());
    assertNotNull(updated.getExpectedArrivalDate());
  }

  @Test
  void receivePurchaseOrderItem_whenAlreadyFullyReceived_shouldThrow() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setQuantity(5);
    poi.setReceivedQuantity(5);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.receivePurchaseOrderItem(11, null));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void receivePurchaseOrderItem_whenPoItemIdNull_shouldThrow() {
    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.receivePurchaseOrderItem(null, 1));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }
}

