package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.PurchaseOrder;
import java.util.List;

public interface PurchaseOrderMapper {
    int deleteByPoId(Integer poId);
    int insert(PurchaseOrder record);
    PurchaseOrder selectByPoId(Integer poId);
    int updateByPoId(PurchaseOrder record);
    List<PurchaseOrder> selectAll();
}


