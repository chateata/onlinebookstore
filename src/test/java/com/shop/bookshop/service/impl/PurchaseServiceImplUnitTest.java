package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.PurchaseOrderItemMapper;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import com.shop.bookshop.dao.PublisherMapper;
import com.shop.bookshop.dao.ShortageMapper;
import com.shop.bookshop.dao.SupplierBookMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Publisher;
import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.pojo.PurchaseOrderItem;
import com.shop.bookshop.pojo.Shortage;
import com.shop.bookshop.pojo.SupplierBook;
import com.shop.bookshop.util.ResultCode;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceImplUnitTest {

  private PurchaseServiceImpl service;

  @Mock private PurchaseOrderMapper purchaseOrderMapper;
  @Mock private PurchaseOrderItemMapper purchaseOrderItemMapper;
  @Mock private BookMapper bookMapper;
  @Mock private ShortageMapper shortageMapper;
  @Mock private SupplierBookMapper supplierBookMapper;
  @Mock private PublisherMapper publisherMapper;

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
    service = new PurchaseServiceImpl();
    setField(service, "purchaseOrderMapper", purchaseOrderMapper);
    setField(service, "purchaseOrderItemMapper", purchaseOrderItemMapper);
    setField(service, "bookMapper", bookMapper);
    setField(service, "shortageMapper", shortageMapper);
    setField(service, "supplierBookMapper", supplierBookMapper);
    setField(service, "publisherMapper", publisherMapper);
  }

  @Test
  void createPurchaseOrder_whenNullOrEmptyItems_shouldThrow() {
    CustomizeException ex1 =
        assertThrows(CustomizeException.class, () -> service.createPurchaseOrder(null));
    assertEquals(ResultCode.FAILED.getCode(), ex1.getCode());

    PurchaseOrder po2 = new PurchaseOrder();
    po2.setItems(null);
    CustomizeException ex2 =
        assertThrows(CustomizeException.class, () -> service.createPurchaseOrder(po2));
    assertEquals(ResultCode.FAILED.getCode(), ex2.getCode());

    PurchaseOrder po3 = new PurchaseOrder();
    po3.setItems(Collections.emptyList());
    CustomizeException ex3 =
        assertThrows(CustomizeException.class, () -> service.createPurchaseOrder(po3));
    assertEquals(ResultCode.FAILED.getCode(), ex3.getCode());
  }

  @Test
  void createPurchaseOrder_shouldComputeTotal_setDefaults_andInsertItems() {
    PurchaseOrder po = new PurchaseOrder();
    po.setOrderDate(null);
    po.setStatus(null);
    po.setTotalAmount(null);

    PurchaseOrderItem item1 = new PurchaseOrderItem();
    item1.setQuantity(2);
    item1.setUnitPrice(new BigDecimal("3.00"));

    PurchaseOrderItem item2 = new PurchaseOrderItem();
    item2.setQuantity(1);
    item2.setUnitPrice(new BigDecimal("1.50"));

    po.setItems(Arrays.asList(item1, item2));

    when(purchaseOrderMapper.insert(any(PurchaseOrder.class)))
        .thenAnswer(
            inv -> {
              PurchaseOrder inserted = inv.getArgument(0);
              inserted.setPoId(22);
              return 1;
            });

    Integer poId = service.createPurchaseOrder(po);

    assertEquals(Integer.valueOf(22), poId);
    assertNotNull(po.getOrderDate());
    assertEquals("PENDING", po.getStatus());
    assertEquals(new BigDecimal("7.50"), po.getTotalAmount());
    assertEquals(Integer.valueOf(0), item1.getReceivedQuantity());
    assertEquals(Integer.valueOf(0), item2.getReceivedQuantity());
    assertEquals(Integer.valueOf(22), item1.getPoId());
    assertEquals(Integer.valueOf(22), item2.getPoId());
    verify(purchaseOrderMapper).insert(po);
    verify(purchaseOrderItemMapper).insert(item1);
    verify(purchaseOrderItemMapper).insert(item2);
  }

  @Test
  void createPurchaseOrder_whenUnitPriceNull_shouldUseSupplierBookSupplyPriceThenPrice() {
    PurchaseOrder po = new PurchaseOrder();
    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setSupplierBookId(9);
    item.setQuantity(2);
    item.setUnitPrice(null);
    po.setItems(Collections.singletonList(item));

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setSupplyPrice(new BigDecimal("4.00"));
    sb.setPrice(new BigDecimal("5.00"));
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);
    when(purchaseOrderMapper.insert(any(PurchaseOrder.class)))
        .thenAnswer(
            inv -> {
              PurchaseOrder inserted = inv.getArgument(0);
              inserted.setPoId(22);
              return 1;
            });

    service.createPurchaseOrder(po);

    assertEquals(new BigDecimal("4.00"), item.getUnitPrice());
    assertEquals(new BigDecimal("8.00"), po.getTotalAmount());
  }

  @Test
  void createPurchaseOrder_whenSupplierBookHasNoSupplyPrice_shouldUsePrice() {
    PurchaseOrder po = new PurchaseOrder();
    po.setOrderDate(new Date()); // cover non-null orderDate branch
    po.setStatus("CUSTOM");
    po.setTotalAmount(new BigDecimal("123.45")); // cover non-null totalAmount branch

    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setSupplierBookId(9);
    item.setQuantity(null); // cover qty==null branch
    item.setUnitPrice(null);
    item.setReceivedQuantity(1); // cover receivedQuantity != null branch
    po.setItems(Collections.singletonList(item));

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setSupplyPrice(null);
    sb.setPrice(new BigDecimal("5.00"));
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);
    when(purchaseOrderMapper.insert(any(PurchaseOrder.class)))
        .thenAnswer(
            inv -> {
              PurchaseOrder inserted = inv.getArgument(0);
              inserted.setPoId(22);
              return 1;
            });

    service.createPurchaseOrder(po);

    assertEquals(new BigDecimal("5.00"), item.getUnitPrice());
    assertEquals(new BigDecimal("123.45"), po.getTotalAmount());
    assertEquals("CUSTOM", po.getStatus());
  }

  @Test
  void createPurchaseOrder_whenUnitPriceNullAndBookIdProvided_shouldUseBookPriceOrZero() {
    PurchaseOrder po = new PurchaseOrder();
    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setBookId(33);
    item.setQuantity(2);
    item.setUnitPrice(null);
    po.setItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(33);
    book.setPrice(new BigDecimal("1.25"));
    when(bookMapper.selectByBookId(33)).thenReturn(book);
    when(purchaseOrderMapper.insert(any(PurchaseOrder.class)))
        .thenAnswer(
            inv -> {
              PurchaseOrder inserted = inv.getArgument(0);
              inserted.setPoId(22);
              return 1;
            });

    service.createPurchaseOrder(po);

    assertEquals(new BigDecimal("1.25"), item.getUnitPrice());
    assertEquals(new BigDecimal("2.50"), po.getTotalAmount());
  }

  @Test
  void createPurchaseOrder_whenBookPriceMissing_shouldUseZero() {
    PurchaseOrder po = new PurchaseOrder();
    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setBookId(33);
    item.setQuantity(2);
    item.setUnitPrice(null);
    po.setItems(Collections.singletonList(item));

    Book book = new Book();
    book.setBookId(33);
    book.setPrice(null);
    when(bookMapper.selectByBookId(33)).thenReturn(book);
    when(purchaseOrderMapper.insert(any(PurchaseOrder.class)))
        .thenAnswer(
            inv -> {
              PurchaseOrder inserted = inv.getArgument(0);
              inserted.setPoId(22);
              return 1;
            });

    service.createPurchaseOrder(po);

    assertEquals(BigDecimal.ZERO, item.getUnitPrice());
    assertEquals(new BigDecimal("0.00"), po.getTotalAmount());
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
    when(purchaseOrderItemMapper.selectByPoId(22))
        .thenReturn(Collections.singletonList(refreshed));

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

  @Test
  void receivePurchaseOrderItem_whenPoiMissing_shouldThrow() {
    when(purchaseOrderItemMapper.selectByPoItemId(999)).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.receivePurchaseOrderItem(999, 1));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void receivePurchaseOrderItem_whenBookNull_shouldThrow() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setQuantity(1);
    poi.setReceivedQuantity(null); // cover oldReceived null branch
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    when(bookMapper.selectByBookId(33)).thenReturn(null);

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.receivePurchaseOrderItem(11, 1));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }

  @Test
  void receivePurchaseOrderItem_whenBookStockNull_shouldTreatAsZeroAndAdd() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    Book book = new Book();
    book.setBookId(33);
    book.setStock(null);
    when(bookMapper.selectByBookId(33)).thenReturn(book);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 1);

    assertEquals(Integer.valueOf(1), book.getStock());
    verify(bookMapper).updateByBookId(book);
  }

  @Test
  void receivePurchaseOrderItem_whenSupplierBookNull_shouldSkipSupplierFlow() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setSupplierBookId(9);
    poi.setBookId(null);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(null);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 1);

    verify(bookMapper, never()).insert(any(Book.class));
  }

  @Test
  void receivePurchaseOrderItem_whenAddedReceivedNull_shouldReceiveRemaining_andCompleteOrder() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setQuantity(5);
    poi.setReceivedQuantity(3);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    Book book = new Book();
    book.setBookId(33);
    book.setStock(10);
    when(bookMapper.selectByBookId(33)).thenReturn(book);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(null);
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);

    PurchaseOrderItem refreshed = new PurchaseOrderItem();
    refreshed.setQuantity(5);
    refreshed.setReceivedQuantity(5);
    when(purchaseOrderItemMapper.selectByPoId(22))
        .thenReturn(Collections.singletonList(refreshed));

    service.receivePurchaseOrderItem(11, null);

    assertEquals(Integer.valueOf(5), poi.getReceivedQuantity());
    assertEquals(Integer.valueOf(12), book.getStock());
    ArgumentCaptor<PurchaseOrder> poCaptor = ArgumentCaptor.forClass(PurchaseOrder.class);
    verify(purchaseOrderMapper).updateByPoId(poCaptor.capture());
    assertEquals("COMPLETED", poCaptor.getValue().getStatus());
    assertNotNull(poCaptor.getValue().getExpectedArrivalDate());
  }

  @Test
  void receivePurchaseOrderItem_whenSupplierBookAndNoExistingIsbn_shouldCreateBook_andUpdatePoiBookId() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setSupplierBookId(9);
    poi.setBookId(null);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setIsbn("ISBN-X");
    sb.setTitle("T");
    sb.setAuthor("A");
    sb.setPress("P");
    sb.setPrice(new BigDecimal("9.99"));
    sb.setSeriesId(1);
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);

    when(bookMapper.selectByIsbn("ISBN-X")).thenReturn(Collections.emptyList());
    when(publisherMapper.selectByName("P")).thenReturn(null);
    when(publisherMapper.insert(any(Publisher.class)))
        .thenAnswer(
            inv -> {
              Publisher p = inv.getArgument(0);
              p.setPublisherId(77);
              return 1;
            });
    when(bookMapper.insert(any(Book.class)))
        .thenAnswer(
            inv -> {
              Book b = inv.getArgument(0);
              b.setBookId(101);
              return 1;
            });

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 3);

    assertEquals(Integer.valueOf(3), poi.getReceivedQuantity());
    assertEquals(Integer.valueOf(101), poi.getBookId());
    verify(bookMapper).insert(any(Book.class));
    verify(purchaseOrderItemMapper, times(2)).updateByPoItemId(poi);
  }

  @Test
  void receivePurchaseOrderItem_whenSupplierBookAndExistingIsbn_shouldAddStock_andUpdatePoiBookId() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setSupplierBookId(9);
    poi.setBookId(null);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setIsbn("ISBN-X");
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);

    Book existing = new Book();
    existing.setBookId(55);
    existing.setStock(10);
    when(bookMapper.selectByIsbn("ISBN-X")).thenReturn(Collections.singletonList(existing));

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 2);

    assertEquals(Integer.valueOf(2), poi.getReceivedQuantity());
    assertEquals(Integer.valueOf(55), poi.getBookId());
    assertEquals(Integer.valueOf(12), existing.getStock());
    verify(bookMapper).updateByBookId(existing);
    verify(purchaseOrderItemMapper, times(2)).updateByPoItemId(poi);
    verify(bookMapper, never()).insert(any(Book.class));
  }

  @Test
  void receivePurchaseOrderItem_whenShortageCovered_shouldMarkProcessed() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setShortageId(44);
    poi.setQuantity(10);
    poi.setReceivedQuantity(3);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    Book book = new Book();
    book.setBookId(33);
    book.setStock(0);
    when(bookMapper.selectByBookId(33)).thenReturn(book);

    Shortage s = new Shortage();
    s.setShortageId(44);
    s.setQuantity(5);
    s.setIsProcessed(false);
    when(shortageMapper.selectByShortageId(44)).thenReturn(s);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 2); // newReceived=5 covers shortage qty

    assertEquals(Boolean.TRUE, s.getIsProcessed());
    verify(shortageMapper).updateByShortageId(s);
  }

  @Test
  void receivePurchaseOrderItem_whenShortageAlreadyProcessed_shouldNotUpdateShortage() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setBookId(33);
    poi.setShortageId(44);
    poi.setQuantity(10);
    poi.setReceivedQuantity(3);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    Book book = new Book();
    book.setBookId(33);
    book.setStock(0);
    when(bookMapper.selectByBookId(33)).thenReturn(book);

    Shortage s = new Shortage();
    s.setShortageId(44);
    s.setQuantity(5);
    s.setIsProcessed(true);
    when(shortageMapper.selectByShortageId(44)).thenReturn(s);

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 2);

    verify(shortageMapper, never()).updateByShortageId(any(Shortage.class));
  }

  @Test
  void receivePurchaseOrderItem_whenSupplierBookIsbnBlank_shouldSkipIsbnLookup_andCreateNewBook() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setSupplierBookId(9);
    poi.setBookId(null);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setIsbn("  "); // blank => should not call selectByIsbn
    sb.setTitle("T");
    sb.setAuthor("A");
    sb.setPress("P");
    sb.setPrice(new BigDecimal("9.99"));
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);

    when(publisherMapper.selectByName("P")).thenReturn(null);
    when(publisherMapper.insert(any(Publisher.class)))
        .thenAnswer(
            inv -> {
              Publisher p = inv.getArgument(0);
              p.setPublisherId(77);
              return 1;
            });
    when(bookMapper.insert(any(Book.class)))
        .thenAnswer(
            inv -> {
              Book b = inv.getArgument(0);
              b.setBookId(101);
              return 1;
            });

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 1);

    verify(bookMapper, never()).selectByIsbn(any(String.class));
    verify(bookMapper).insert(any(Book.class));
  }

  @Test
  void receivePurchaseOrderItem_whenPublisherLookupThrows_shouldStillCreateBook() {
    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(11);
    poi.setPoId(22);
    poi.setSupplierBookId(9);
    poi.setBookId(null);
    poi.setQuantity(10);
    poi.setReceivedQuantity(0);
    when(purchaseOrderItemMapper.selectByPoItemId(11)).thenReturn(poi);

    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(9);
    sb.setIsbn("ISBN-X");
    sb.setTitle("T");
    sb.setAuthor("A");
    sb.setPress("P");
    sb.setPrice(new BigDecimal("9.99"));
    when(supplierBookMapper.selectBySupplierBookId(9)).thenReturn(sb);

    when(bookMapper.selectByIsbn("ISBN-X")).thenReturn(Collections.emptyList());
    when(publisherMapper.selectByName("P")).thenThrow(new RuntimeException("boom"));
    when(bookMapper.insert(any(Book.class)))
        .thenAnswer(
            inv -> {
              Book b = inv.getArgument(0);
              b.setBookId(101);
              return 1;
            });

    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(22);
    po.setExpectedArrivalDate(new Date());
    when(purchaseOrderMapper.selectByPoId(22)).thenReturn(po);
    when(purchaseOrderItemMapper.selectByPoId(22)).thenReturn(Collections.singletonList(poi));

    service.receivePurchaseOrderItem(11, 1);

    verify(bookMapper).insert(any(Book.class));
  }
}

