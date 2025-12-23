package com.shop.bookshop.service;

import com.shop.bookshop.pojo.ShoppingCart;

import java.util.List;


public interface ShoppingCartService {

    int addToShoppingCart(ShoppingCart cart);

    int deleteShoppingCarts(int[] cartIds);

    int deleteShoppingCartByCartId(Integer cartId);

    int updateShoppingCart(ShoppingCart cart);

    List<ShoppingCart> getShoppingCartsByUserId(Integer userId);
}
