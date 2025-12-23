package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.PurchaseOrderItem;
import java.util.List;

public interface PurchaseOrderItemMapper {
    int deleteByPoItemId(Integer poItemId);
    int insert(PurchaseOrderItem record);
    PurchaseOrderItem selectByPoItemId(Integer poItemId);
    int updateByPoItemId(PurchaseOrderItem record);
    List<PurchaseOrderItem> selectByPoId(Integer poId);
}


