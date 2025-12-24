package com.shop.bookshop.controller;

import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.service.PurchaseService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private com.shop.bookshop.dao.PurchaseOrderItemMapper purchaseOrderItemMapper;

    @GetMapping("/list")
    public ResultVO<Object> list(@RequestParam(value = "poId", required = false) Integer poId) {
        if (poId != null) {
            PurchaseOrder po = purchaseOrderMapper.selectByPoId(poId);
            if (po != null) {
                po.setItems(purchaseOrderItemMapper.selectByPoId(poId));
                return new ResultVO<>(ResultCode.SUCCESS, 1, po);
            } else {
                return new ResultVO<>(ResultCode.RECORD_NOT_FOUND, null);
            }
        }
        List<PurchaseOrder> list = purchaseOrderMapper.selectAll();
        int total = list == null ? 0 : list.size();
        return new ResultVO<>(ResultCode.SUCCESS, total, list);
    }

    @PostMapping("/create")
    public ResultVO<Integer> create(@RequestBody PurchaseOrder purchaseOrder) {
        Integer poId = purchaseService.createPurchaseOrder(purchaseOrder);
        return new ResultVO<>(ResultCode.SUCCESS, poId);
    }

    @PostMapping("/receive")
    public ResultVO<Void> receive(@RequestParam Integer poItemId, @RequestParam(required = false) Integer addedReceived) {
        if (poItemId == null) {
            return ResultVO.error(ResultCode.FAILED, "参数错误");
        }
        if (addedReceived == null) {
            // compute remaining
            com.shop.bookshop.pojo.PurchaseOrderItem poi = purchaseOrderItemMapper.selectByPoItemId(poItemId);
            if (poi == null) {
                return ResultVO.error(ResultCode.FAILED, "采购单项不存在");
            }
            int qty = poi.getQuantity() == null ? 0 : poi.getQuantity();
            int recv = poi.getReceivedQuantity() == null ? 0 : poi.getReceivedQuantity();
            addedReceived = Math.max(0, qty - recv);
        }
        purchaseService.receivePurchaseOrderItem(poItemId, addedReceived);
        return ResultVO.success(null);
    }
}


