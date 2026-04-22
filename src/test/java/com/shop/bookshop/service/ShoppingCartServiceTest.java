package com.shop.bookshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.shop.bookshop.pojo.ShoppingCart;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class ShoppingCartServiceTest {
  @Autowired private ShoppingCartService shoppingCartService;

  @Test
  public void testAddToShoppingCart() {
    ShoppingCart cart = new ShoppingCart();
    cart.setUserId(2020003);
    cart.setBookId(1);
    cart.setPrice(new BigDecimal(25.34));
    cart.setQuantity(120);
    int result = shoppingCartService.addToShoppingCart(cart);
    assertEquals(1, result);
  }

  @Test
  @Transactional
  public void testDeleteShoppingCartByCartId() {
    assertEquals(1, shoppingCartService.deleteShoppingCartByCartId(3));
  }

  @Test
  public void testUpdateShoppingCart() {
    ShoppingCart cart = new ShoppingCart();
    cart.setCartId(4);
    cart.setPrice(new BigDecimal(99.34));
    cart.setQuantity(999);
    assertEquals(1, shoppingCartService.updateShoppingCart(cart));
  }
}
