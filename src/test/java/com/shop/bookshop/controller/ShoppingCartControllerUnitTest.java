package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Author;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.ShoppingCart;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.ShoppingCartService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartControllerUnitTest {

  private ShoppingCartController controller;

  @Mock private ShoppingCartService shoppingCartService;
  @Mock private HttpSession session;

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
    controller = new ShoppingCartController();
    setField(controller, "shoppingCartService", shoppingCartService);
  }

  @Test
  void getCartByUserId_shouldEnrichAuthorsDisplay_bestEffort() {
    User user = new User();
    user.setUserId(7);
    when(session.getAttribute("user")).thenReturn(user);

    ShoppingCart c1 = new ShoppingCart();
    Book b1 = new Book();
    b1.setAuthor("fallback");
    Author a1 = new Author();
    a1.setName("A");
    Author a2 = new Author();
    a2.setName("B");
    b1.setAuthors(Arrays.asList(a1, null, a2));
    c1.setBook_info(b1);

    ShoppingCart c2 = new ShoppingCart();
    Book b2 = new Book();
    b2.setAuthor("fallback2");
    b2.setAuthors(new ArrayList<>()); // empty list -> fallback to author
    c2.setBook_info(b2);

    ShoppingCart c3 = new ShoppingCart();
    c3.setBook_info(null); // no enrichment

    List<ShoppingCart> carts = Arrays.asList(c1, c2, c3);
    when(shoppingCartService.getShoppingCartsByUserId(7)).thenReturn(carts);

    ResultVO r = controller.getCartByUserId(session);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(carts, r.getData());
    assertEquals("A, B", c1.getBook_info().getAuthorsDisplay());
    assertNotNull(c2.getBook_info().getAuthorsDisplay());
    assertEquals("fallback2", c2.getBook_info().getAuthorsDisplay());
  }

  @Test
  void updateCartItem_whenQtyInvalid_shouldThrow() {
    assertThrows(CustomizeException.class, () -> controller.updateCartItem(1, 0));
    assertThrows(CustomizeException.class, () -> controller.updateCartItem(1, 11));
  }

  @Test
  void updateCartItem_whenOk_shouldCallService_andReturnSuccess() {
    ResultVO r = controller.updateCartItem(5, 2);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(shoppingCartService).updateShoppingCart(any(ShoppingCart.class));
  }

  @Test
  void addToShoppingCart_shouldSetUserId_andReturnSuccess() {
    User user = new User();
    user.setUserId(7);
    when(session.getAttribute("user")).thenReturn(user);

    ShoppingCart cart = new ShoppingCart();
    cart.setBookId(9);
    cart.setQuantity(1);

    ResultVO r = controller.addToShoppingCart(cart, session);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(Integer.valueOf(7), cart.getUserId());
    verify(shoppingCartService).addToShoppingCart(eq(cart));
  }

  @Test
  void deleteCartItem_singleAndBatch_shouldReturnSuccess() {
    ResultVO r1 = controller.deleteCartItem(1);
    assertEquals(ResultCode.SUCCESS.getCode(), r1.getCode());
    verify(shoppingCartService).deleteShoppingCartByCartId(1);

    int[] ids = new int[] {1, 2};
    ResultVO r2 = controller.deleteCartItem(ids);
    assertEquals(ResultCode.SUCCESS.getCode(), r2.getCode());
    verify(shoppingCartService).deleteShoppingCarts(ids);
  }
}

