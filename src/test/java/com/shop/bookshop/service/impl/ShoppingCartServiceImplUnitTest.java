package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.ShoppingCartMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.ShoppingCart;
import com.shop.bookshop.util.ResultCode;
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
public class ShoppingCartServiceImplUnitTest {

  private ShoppingCartServiceImpl service;

  @Mock private ShoppingCartMapper shoppingCartMapper;

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
    service = new ShoppingCartServiceImpl();
    setField(service, "shoppingCartMapper", shoppingCartMapper);
  }

  @Test
  void addToShoppingCart_whenNotExists_shouldInsert() {
    ShoppingCart cart = new ShoppingCart();
    cart.setUserId(7);
    cart.setBookId(99);
    when(shoppingCartMapper.selectByUserIdAndBookId(7, 99)).thenReturn(null);
    when(shoppingCartMapper.insert(cart)).thenReturn(1);

    int inserted = service.addToShoppingCart(cart);

    assertEquals(1, inserted);
    verify(shoppingCartMapper).selectByUserIdAndBookId(7, 99);
    verify(shoppingCartMapper).insert(cart);
  }

  @Test
  void addToShoppingCart_whenAlreadyExists_shouldThrowAndNotInsert() {
    ShoppingCart cart = new ShoppingCart();
    cart.setUserId(7);
    cart.setBookId(99);
    when(shoppingCartMapper.selectByUserIdAndBookId(7, 99)).thenReturn(new ShoppingCart());

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> service.addToShoppingCart(cart));

    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
    verify(shoppingCartMapper).selectByUserIdAndBookId(7, 99);
    verify(shoppingCartMapper, never()).insert(any(ShoppingCart.class));
  }

  @Test
  void deleteShoppingCarts_whenAllExist_shouldSumDeletes() {
    when(shoppingCartMapper.deleteByCartId(1)).thenReturn(1);
    when(shoppingCartMapper.deleteByCartId(2)).thenReturn(1);
    when(shoppingCartMapper.deleteByCartId(3)).thenReturn(1);

    int total = service.deleteShoppingCarts(new int[] {1, 2, 3});

    assertEquals(3, total);
    verify(shoppingCartMapper).deleteByCartId(1);
    verify(shoppingCartMapper).deleteByCartId(2);
    verify(shoppingCartMapper).deleteByCartId(3);
  }

  @Test
  void deleteShoppingCarts_whenSomeMissing_shouldSumOnlySuccesses() {
    when(shoppingCartMapper.deleteByCartId(1)).thenReturn(1);
    when(shoppingCartMapper.deleteByCartId(2)).thenReturn(0);
    when(shoppingCartMapper.deleteByCartId(3)).thenReturn(1);

    int total = service.deleteShoppingCarts(new int[] {1, 2, 3});

    assertEquals(2, total);
  }

  @Test
  void deleteShoppingCarts_whenEmptyArray_shouldReturnZero() {
    int total = service.deleteShoppingCarts(new int[] {});

    assertEquals(0, total);
    verify(shoppingCartMapper, never()).deleteByCartId(any(Integer.class));
  }

  @Test
  void deleteShoppingCartByCartId_shouldReturnMapperResult() {
    when(shoppingCartMapper.deleteByCartId(1)).thenReturn(1);
    when(shoppingCartMapper.deleteByCartId(999)).thenReturn(0);

    assertEquals(1, service.deleteShoppingCartByCartId(1));
    assertEquals(0, service.deleteShoppingCartByCartId(999));
  }

  @Test
  void updateShoppingCart_shouldReturnMapperResult() {
    ShoppingCart cart = new ShoppingCart();
    cart.setCartId(1);
    when(shoppingCartMapper.updateByByCartId(cart)).thenReturn(1);

    int updated = service.updateShoppingCart(cart);

    assertEquals(1, updated);
    verify(shoppingCartMapper).updateByByCartId(cart);
  }

  @Test
  void getShoppingCartsByUserId_shouldReturnMapperResult() {
    List<ShoppingCart> list = Arrays.asList(new ShoppingCart(), new ShoppingCart());
    when(shoppingCartMapper.selectByUserId(7)).thenReturn(list);

    List<ShoppingCart> got = service.getShoppingCartsByUserId(7);

    assertEquals(2, got.size());
    verify(shoppingCartMapper).selectByUserId(7);
  }

  @Test
  void getShoppingCartsByUserId_whenEmpty_shouldReturnEmptyList() {
    when(shoppingCartMapper.selectByUserId(7)).thenReturn(Collections.emptyList());

    List<ShoppingCart> got = service.getShoppingCartsByUserId(7);

    assertEquals(0, got.size());
    verify(shoppingCartMapper).selectByUserId(7);
  }
}

