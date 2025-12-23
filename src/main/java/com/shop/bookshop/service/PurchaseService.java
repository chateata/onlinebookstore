package com.shop.bookshop.service;

import com.shop.bookshop.pojo.PurchaseOrder;

import java.util.List;

public interface PurchaseService {
    /**
     * 创建采购单及其项，返回生成的采购单ID
     */
    Integer createPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 处理采购单项收货：增加已收数量、更新库存、标记缺书记录、并更新采购单状态
     * @param poItemId 采购单项ID
     * @param addedReceived 本次新增已收数量（非覆盖）
     */
    void receivePurchaseOrderItem(Integer poItemId, Integer addedReceived);
}


