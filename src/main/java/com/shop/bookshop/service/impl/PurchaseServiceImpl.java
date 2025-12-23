package com.shop.bookshop.service.impl;

import com.shop.bookshop.dao.PurchaseOrderItemMapper;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import com.shop.bookshop.dao.ShortageMapper;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.pojo.PurchaseOrderItem;
import com.shop.bookshop.pojo.Shortage;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.service.PurchaseService;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private PurchaseOrderItemMapper purchaseOrderItemMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private ShortageMapper shortageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null || purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new CustomizeException(ResultCode.FAILED, "采购单或采购单项不能为空");
        }
        purchaseOrderMapper.insert(purchaseOrder);
        Integer poId = purchaseOrder.getPoId();
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            item.setPoId(poId);
            purchaseOrderItemMapper.insert(item);
        }
        return poId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receivePurchaseOrderItem(Integer poItemId, Integer addedReceived) {
        if (poItemId == null || addedReceived == null || addedReceived <= 0) {
            throw new CustomizeException(ResultCode.FAILED, "参数错误");
        }
        PurchaseOrderItem poi = purchaseOrderItemMapper.selectByPoItemId(poItemId);
        if (poi == null) {
            throw new CustomizeException(ResultCode.FAILED, "采购单项不存在");
        }
        int oldReceived = poi.getReceivedQuantity() == null ? 0 : poi.getReceivedQuantity();
        int newReceived = oldReceived + addedReceived;
        // 更新采购项已收数量
        poi.setReceivedQuantity(newReceived);
        purchaseOrderItemMapper.updateByPoItemId(poi);
        // 增加库存
        Book book = bookMapper.selectByBookId(poi.getBookId());
        if (book == null) {
            throw new CustomizeException(ResultCode.FAILED, "书籍不存在");
        }
        book.setStock(book.getStock() + addedReceived);
        bookMapper.updateByBookId(book);
        // 若关联了 shortage，且已收数量覆盖缺书数量，则标记缺书为已处理
        if (poi.getShortageId() != null) {
            Shortage s = shortageMapper.selectByShortageId(poi.getShortageId());
            if (s != null && (s.getIsProcessed() == null || !s.getIsProcessed())) {
                if (newReceived >= (s.getQuantity() == null ? 0 : s.getQuantity())) {
                    s.setIsProcessed(true);
                    shortageMapper.updateByShortageId(s);
                }
            }
        }
        // 更新采购单状态（简单策略：若所有项已收齐设为 COMPLETED，否则 PARTIAL）
        PurchaseOrder po = purchaseOrderMapper.selectByPoId(poi.getPoId());
        if (po != null) {
            boolean allReceived = true;
            List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByPoId(po.getPoId());
            for (PurchaseOrderItem it : items) {
                int qty = it.getQuantity() == null ? 0 : it.getQuantity();
                int recv = it.getReceivedQuantity() == null ? 0 : it.getReceivedQuantity();
                if (recv < qty) {
                    allReceived = false;
                    break;
                }
            }
            if (allReceived) {
                po.setStatus("COMPLETED");
            } else {
                po.setStatus("PARTIAL");
            }
            purchaseOrderMapper.updateByPoId(po);
        }
    }
}


