package com.shop.bookshop.service.impl;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.dao.ShoppingCartMapper;
import com.shop.bookshop.pojo.ShoppingCart;
import com.shop.bookshop.service.ShoppingCartService;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 添加购物车
     * @param cart
     * @return
     */
    @Override
    public int addToShoppingCart(ShoppingCart cart) {
        ShoppingCart record = shoppingCartMapper.selectByUserIdAndBookId(cart.getUserId(), cart.getBookId());
        if (record != null) {
            //如果购物车存在该商品的
            throw new CustomizeException(ResultCode.FAILED, "您已经添加过该商品了,不能重复添加哦");
        }
        return shoppingCartMapper.insert(cart);
    }

    /**
     * 批量删除购物车ID
     * @param cartIds  购物车ID数组
     * @return
     */
    @Override
    public int deleteShoppingCarts(int[] cartIds) {
        int total=0;
        for (Integer cartId:cartIds) {
            total += deleteShoppingCartByCartId(cartId);
        }
        return total;
    }

    /**
     * 根据ID删除购物车
     * @param cartId
     * @return
     */
    @Override
    public int deleteShoppingCartByCartId(Integer cartId) {
        return shoppingCartMapper.deleteByCartId(cartId);
    }

    /**
     * 更新购物车
     * @param cart
     * @return
     */
    @Override
    public int updateShoppingCart(ShoppingCart cart) {
        return shoppingCartMapper.updateByByCartId(cart);
    }

    /**
     * 根据用户ID获取购物车
     * @param userId
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCartsByUserId(Integer userId) {
        List<ShoppingCart> carts = shoppingCartMapper.selectByUserId(userId);
        return carts;
    }
}
