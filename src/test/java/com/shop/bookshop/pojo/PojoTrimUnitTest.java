package com.shop.bookshop.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class PojoTrimUnitTest {

  @Test
  void book_stringSettersShouldTrim_andAllowNull() {
    Book b = new Book();

    b.setCategoryCode("  C  ");
    b.setBookName("  N  ");
    b.setIsbn("  I  ");
    b.setAuthor("  A  ");
    b.setPress("  P  ");
    b.setImage("  img  ");
    b.setDescription("  d  ");

    assertEquals("C", b.getCategoryCode());
    assertEquals("N", b.getBookName());
    assertEquals("I", b.getIsbn());
    assertEquals("A", b.getAuthor());
    assertEquals("P", b.getPress());
    assertEquals("img", b.getImage());
    assertEquals("d", b.getDescription());

    b.setCategoryCode(null);
    b.setBookName(null);
    b.setIsbn(null);
    b.setAuthor(null);
    b.setPress(null);
    b.setImage(null);
    b.setDescription(null);

    assertEquals(null, b.getCategoryCode());
    assertEquals(null, b.getBookName());
    assertEquals(null, b.getIsbn());
    assertEquals(null, b.getAuthor());
    assertEquals(null, b.getPress());
    assertEquals(null, b.getImage());
    assertEquals(null, b.getDescription());

    assertNotNull(b.toString());
  }

  @Test
  void user_stringSettersShouldTrim_andAllowNull() {
    User u = new User();

    u.setUserName("  u  ");
    u.setPassword("  p  ");
    u.setEmail("  e@e.com  ");
    u.setAvatar("  a  ");

    assertEquals("u", u.getUserName());
    assertEquals("p", u.getPassword());
    assertEquals("e@e.com", u.getEmail());
    assertEquals("a", u.getAvatar());

    u.setUserName(null);
    u.setPassword(null);
    u.setEmail(null);
    u.setAvatar(null);

    assertEquals(null, u.getUserName());
    assertEquals(null, u.getPassword());
    assertEquals(null, u.getEmail());
    assertEquals(null, u.getAvatar());
  }

  @Test
  void category_stringSettersShouldTrim_andAllowNull_andToString() {
    Category c = new Category();
    c.setCategoryCode("  C1  ");
    c.setCategoryName("  Name  ");
    assertEquals("C1", c.getCategoryCode());
    assertEquals("Name", c.getCategoryName());
    assertNotNull(c.toString());

    c.setCategoryCode(null);
    c.setCategoryName(null);
    assertEquals(null, c.getCategoryCode());
    assertEquals(null, c.getCategoryName());
  }

  @Test
  void order_stringSettersShouldTrim_andAllowNull_andToString() {
    Order o = new Order();
    o.setConsigneeName("  n  ");
    o.setAddress("  addr  ");
    o.setZip("  123456  ");
    o.setPhoneNumber("  13800000000  ");

    assertEquals("n", o.getConsigneeName());
    assertEquals("addr", o.getAddress());
    assertEquals("123456", o.getZip());
    assertEquals("13800000000", o.getPhoneNumber());
    assertNotNull(o.toString());

    o.setConsigneeName(null);
    o.setAddress(null);
    o.setZip(null);
    o.setPhoneNumber(null);
    assertEquals(null, o.getConsigneeName());
    assertEquals(null, o.getAddress());
    assertEquals(null, o.getZip());
    assertEquals(null, o.getPhoneNumber());
  }

  @Test
  void smoke_shouldCoverSimplePojoGetSet() {
    BookSupplier bs = new BookSupplier();
    bs.setBookId(1);
    bs.setSupplierId(2);
    bs.setSupplyPrice(new BigDecimal("3.00"));
    assertEquals(Integer.valueOf(1), bs.getBookId());
    assertEquals(Integer.valueOf(2), bs.getSupplierId());
    assertEquals(new BigDecimal("3.00"), bs.getSupplyPrice());

    BookAuthor ba = new BookAuthor();
    ba.setBookId(1);
    ba.setAuthorId(2);
    ba.setAuthorOrder(3);
    assertEquals(Integer.valueOf(1), ba.getBookId());
    assertEquals(Integer.valueOf(2), ba.getAuthorId());
    assertEquals(Integer.valueOf(3), ba.getAuthorOrder());

    BookKeyword bk = new BookKeyword();
    bk.setBookId(1);
    bk.setKeywordId(2);
    assertEquals(Integer.valueOf(1), bk.getBookId());
    assertEquals(Integer.valueOf(2), bk.getKeywordId());

    Keyword k = new Keyword();
    k.setKeywordId(9);
    k.setWord("w");
    assertEquals(Integer.valueOf(9), k.getKeywordId());
    assertEquals("w", k.getWord());

    Admin a = new Admin();
    a.setAdminName("admin");
    a.setPassword("pw");
    assertEquals("admin", a.getAdminName());
    assertEquals("pw", a.getPassword());

    // OrderItem
    OrderItem oi = new OrderItem();
    oi.setOrderItemId(1);
    oi.setOrderId(2);
    oi.setBookId(3);
    oi.setPrice(new BigDecimal("9.99"));
    oi.setQuantity(4);
    oi.setUnitPrice(new BigDecimal("1.11"));
    oi.setSubtotal(new BigDecimal("4.44"));
    oi.setShippedQuantity(0);
    assertEquals(Integer.valueOf(1), oi.getOrderItemId());
    assertEquals(Integer.valueOf(2), oi.getOrderId());
    assertEquals(Integer.valueOf(3), oi.getBookId());
    assertEquals(new BigDecimal("9.99"), oi.getPrice());
    assertEquals(Integer.valueOf(4), oi.getQuantity());
    assertEquals(new BigDecimal("1.11"), oi.getUnitPrice());
    assertEquals(new BigDecimal("4.44"), oi.getSubtotal());
    assertEquals(Integer.valueOf(0), oi.getShippedQuantity());
    assertNotNull(oi.toString());

    // Shortage
    Shortage s = new Shortage();
    Date now = new Date();
    s.setShortageId(1);
    s.setBookId(2);
    s.setQuantity(3);
    s.setRegisterDate(now);
    s.setSource("SRC");
    s.setIsProcessed(Boolean.TRUE);
    s.setCustomerRequestId(9);
    assertEquals(Integer.valueOf(1), s.getShortageId());
    assertEquals(Integer.valueOf(2), s.getBookId());
    assertEquals(Integer.valueOf(3), s.getQuantity());
    assertEquals(now, s.getRegisterDate());
    assertEquals("SRC", s.getSource());
    assertEquals(Boolean.TRUE, s.getIsProcessed());
    assertEquals(Integer.valueOf(9), s.getCustomerRequestId());

    // Supplier / Publisher / Series
    Supplier sup = new Supplier();
    sup.setSupplierId(1);
    sup.setName("n");
    sup.setContact("c");
    sup.setAddress("a");
    assertEquals(Integer.valueOf(1), sup.getSupplierId());
    assertEquals("n", sup.getName());
    assertEquals("c", sup.getContact());
    assertEquals("a", sup.getAddress());

    Publisher pub = new Publisher();
    pub.setPublisherId(1);
    pub.setName("p");
    pub.setContact("c");
    pub.setAddress("a");
    assertEquals(Integer.valueOf(1), pub.getPublisherId());
    assertEquals("p", pub.getName());
    assertEquals("c", pub.getContact());
    assertEquals("a", pub.getAddress());

    Series series = new Series();
    series.setSeriesId(1);
    series.setSeriesName("sn");
    series.setDescription("d");
    assertEquals(Integer.valueOf(1), series.getSeriesId());
    assertEquals("sn", series.getSeriesName());
    assertEquals("d", series.getDescription());

    // SupplierBook
    SupplierBook sb = new SupplierBook();
    sb.setSupplierBookId(1);
    sb.setSupplierId(2);
    sb.setSeriesId(3);
    sb.setIsbn("i");
    sb.setTitle("t");
    sb.setAuthor("a");
    sb.setPress("p");
    sb.setPrice(new BigDecimal("10.00"));
    sb.setSupplyPrice(new BigDecimal("8.00"));
    sb.setDescription("d");
    sb.setStatus("ACTIVE");
    sb.setCreateTime(now);
    sb.setUpdateTime(now);
    sb.setSupplier(sup);
    sb.setSeries(series);
    assertEquals(Integer.valueOf(1), sb.getSupplierBookId());
    assertEquals(Integer.valueOf(2), sb.getSupplierId());
    assertEquals(Integer.valueOf(3), sb.getSeriesId());
    assertEquals("i", sb.getIsbn());
    assertEquals("t", sb.getTitle());
    assertEquals("a", sb.getAuthor());
    assertEquals("p", sb.getPress());
    assertEquals(new BigDecimal("10.00"), sb.getPrice());
    assertEquals(new BigDecimal("8.00"), sb.getSupplyPrice());
    assertEquals("d", sb.getDescription());
    assertEquals("ACTIVE", sb.getStatus());
    assertEquals(now, sb.getCreateTime());
    assertEquals(now, sb.getUpdateTime());
    assertEquals(sup, sb.getSupplier());
    assertEquals(series, sb.getSeries());

    // ShoppingCart
    ShoppingCart cart = new ShoppingCart();
    cart.setCartId(1);
    cart.setUserId(2);
    cart.setBookId(3);
    cart.setPrice(new BigDecimal("5.00"));
    cart.setQuantity(1);
    cart.setBook_info(new Book());
    assertEquals(Integer.valueOf(1), cart.getCartId());
    assertEquals(Integer.valueOf(2), cart.getUserId());
    assertEquals(Integer.valueOf(3), cart.getBookId());
    assertEquals(new BigDecimal("5.00"), cart.getPrice());
    assertEquals(Integer.valueOf(1), cart.getQuantity());
    assertNotNull(cart.getBook_info());

    // PurchaseOrder / PurchaseOrderItem / CreditLevel
    PurchaseOrder po = new PurchaseOrder();
    po.setPoId(1);
    po.setSupplierId(2);
    po.setOrderDate(now);
    po.setExpectedArrivalDate(now);
    po.setStatus("PENDING");
    po.setTotalAmount(new BigDecimal("1.23"));
    po.setSupplier(sup);
    po.setItems(Collections.<PurchaseOrderItem>emptyList());
    assertEquals(Integer.valueOf(1), po.getPoId());
    assertEquals(Integer.valueOf(2), po.getSupplierId());
    assertEquals(now, po.getOrderDate());
    assertEquals(now, po.getExpectedArrivalDate());
    assertEquals("PENDING", po.getStatus());
    assertEquals(new BigDecimal("1.23"), po.getTotalAmount());
    assertEquals(sup, po.getSupplier());
    assertEquals(Collections.emptyList(), po.getItems());

    PurchaseOrderItem poi = new PurchaseOrderItem();
    poi.setPoItemId(1);
    poi.setPoId(2);
    poi.setBookId(3);
    poi.setSupplierBookId(4);
    poi.setQuantity(5);
    poi.setUnitPrice(new BigDecimal("9.99"));
    poi.setShortageId(6);
    poi.setReceivedQuantity(7);
    poi.setBook(new Book());
    poi.setSupplierBook(sb);
    assertEquals(Integer.valueOf(1), poi.getPoItemId());
    assertEquals(Integer.valueOf(2), poi.getPoId());
    assertEquals(Integer.valueOf(3), poi.getBookId());
    assertEquals(Integer.valueOf(4), poi.getSupplierBookId());
    assertEquals(Integer.valueOf(5), poi.getQuantity());
    assertEquals(new BigDecimal("9.99"), poi.getUnitPrice());
    assertEquals(Integer.valueOf(6), poi.getShortageId());
    assertEquals(Integer.valueOf(7), poi.getReceivedQuantity());
    assertNotNull(poi.getBook());
    assertEquals(sb, poi.getSupplierBook());

    CreditLevel cl = new CreditLevel();
    cl.setLevelId(1);
    cl.setLevelName("L");
    cl.setDiscountRate(new BigDecimal("0.10"));
    cl.setOverdraftLimit(new BigDecimal("0.00"));
    cl.setDescription("d");
    assertEquals(Integer.valueOf(1), cl.getLevelId());
    assertEquals("L", cl.getLevelName());
    assertEquals(new BigDecimal("0.10"), cl.getDiscountRate());
    assertEquals(new BigDecimal("0.00"), cl.getOverdraftLimit());
    assertEquals("d", cl.getDescription());
  }
}

