package com.shop.bookshop.service.impl;

import com.shop.bookshop.dao.PurchaseOrderItemMapper;
import com.shop.bookshop.dao.PurchaseOrderMapper;
import com.shop.bookshop.dao.ShortageMapper;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.SupplierBookMapper;
import com.shop.bookshop.pojo.PurchaseOrder;
import com.shop.bookshop.pojo.PurchaseOrderItem;
import com.shop.bookshop.pojo.Shortage;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.SupplierBook;
import com.shop.bookshop.pojo.Publisher;
import com.shop.bookshop.service.PurchaseService;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

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
    @Resource
    private SupplierBookMapper supplierBookMapper;
    @Resource
    private com.shop.bookshop.dao.PublisherMapper publisherMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null || purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new CustomizeException(ResultCode.FAILED, "采购单或采购单项不能为空");
        }
        
        if (purchaseOrder.getOrderDate() == null) {
            purchaseOrder.setOrderDate(new Date());
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            if (item.getUnitPrice() == null) {
                // 如果是供应商书目，优先使用供应商书目的价格
                if (item.getSupplierBookId() != null) {
                    SupplierBook supplierBook = supplierBookMapper.selectBySupplierBookId(item.getSupplierBookId());
                    if (supplierBook != null && supplierBook.getSupplyPrice() != null) {
                        item.setUnitPrice(supplierBook.getSupplyPrice());
                    } else if (supplierBook != null && supplierBook.getPrice() != null) {
                        item.setUnitPrice(supplierBook.getPrice());
                    } else {
                        item.setUnitPrice(BigDecimal.ZERO);
                    }
                } else if (item.getBookId() != null) {
                    // 普通书籍的价格逻辑
                Book book = bookMapper.selectByBookId(item.getBookId());
                if (book != null && book.getPrice() != null) {
                    item.setUnitPrice(book.getPrice());
                    } else {
                        item.setUnitPrice(BigDecimal.ZERO);
                    }
                } else {
                    item.setUnitPrice(BigDecimal.ZERO);
                }
            }
            Integer qty = item.getQuantity() == null ? 0 : item.getQuantity();
            BigDecimal line = item.getUnitPrice().multiply(new BigDecimal(qty)).setScale(2, BigDecimal.ROUND_HALF_UP);
            totalAmount = totalAmount.add(line);
            if (item.getReceivedQuantity() == null) {
                item.setReceivedQuantity(0);
            }
        }
        if (purchaseOrder.getTotalAmount() == null) {
            purchaseOrder.setTotalAmount(totalAmount);
        }
        if (purchaseOrder.getStatus() == null) {
            purchaseOrder.setStatus("PENDING");
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
        if (poItemId == null) {
            throw new CustomizeException(ResultCode.FAILED, "参数错误");
        }
        PurchaseOrderItem poi = purchaseOrderItemMapper.selectByPoItemId(poItemId);
        if (poi == null) {
            throw new CustomizeException(ResultCode.FAILED, "采购单项不存在");
        }
        int oldReceived = poi.getReceivedQuantity() == null ? 0 : poi.getReceivedQuantity();
        int totalQty = poi.getQuantity() == null ? 0 : poi.getQuantity();
        int remaining = Math.max(0, totalQty - oldReceived);
        int toAdd = (addedReceived == null) ? remaining : Math.min(Math.max(0, addedReceived), remaining);
        if (toAdd <= 0) {
            throw new CustomizeException(ResultCode.FAILED, "无可收货数量");
        }
        int newReceived = oldReceived + toAdd;
        // 更新采购项已收数量
        poi.setReceivedQuantity(newReceived);
        purchaseOrderItemMapper.updateByPoItemId(poi);
        // 增加库存（仅对普通书籍增加库存，供应商书目不增加库存）
        if (poi.getBookId() != null) {
        Book book = bookMapper.selectByBookId(poi.getBookId());
        if (book == null) {
            throw new CustomizeException(ResultCode.FAILED, "书籍不存在");
        }
        book.setStock((book.getStock() == null ? 0 : book.getStock()) + toAdd);
        bookMapper.updateByBookId(book);
        }
        // 处理供应商书目：自动将其添加到系统书籍库中（如果不存在）
        if (poi.getSupplierBookId() != null) {
            SupplierBook supplierBook = supplierBookMapper.selectBySupplierBookId(poi.getSupplierBookId());
            if (supplierBook != null) {
                // 检查系统中是否已存在此书籍（通过ISBN匹配）
                Book existingBook = null;
                if (supplierBook.getIsbn() != null && !supplierBook.getIsbn().trim().isEmpty()) {
                    List<Book> booksWithSameIsbn = bookMapper.selectByIsbn(supplierBook.getIsbn());
                    if (booksWithSameIsbn != null && !booksWithSameIsbn.isEmpty()) {
                        existingBook = booksWithSameIsbn.get(0);
                    }
                }

                if (existingBook == null) {
                    // 书籍不存在，自动添加到书籍表
                    Book newBook = new Book();
                    newBook.setBookName(supplierBook.getTitle());
                    newBook.setIsbn(supplierBook.getIsbn());
                    newBook.setAuthor(supplierBook.getAuthor());
                    newBook.setPress(supplierBook.getPress());
                    newBook.setPrice(supplierBook.getPrice());
                    newBook.setStock(toAdd); // 设置收货数量为初始库存
                    newBook.setDescription(supplierBook.getDescription());
                    newBook.setCategoryCode("default"); // 设置为默认分类
                    newBook.setSeriesId(supplierBook.getSeriesId()); // 设置丛书ID
                    newBook.setPubDate(new Date()); // 设置出版日期为当前日期（供应商书目的默认值）
                    newBook.setCreateTime(new Date());

                    // 处理出版社信息
                    if (supplierBook.getPress() != null && !supplierBook.getPress().trim().isEmpty()) {
                        try {
                            // 查找是否已存在相同名称的出版社
                            Publisher existingPublisher = publisherMapper.selectByName(supplierBook.getPress());
                            if (existingPublisher == null) {
                                // 创建新出版社
                                Publisher newPublisher = new Publisher();
                                newPublisher.setName(supplierBook.getPress());
                                publisherMapper.insert(newPublisher);
                                newBook.setPublisherId(newPublisher.getPublisherId());
                            } else {
                                newBook.setPublisherId(existingPublisher.getPublisherId());
                            }
                        } catch (Exception e) {
                            // 如果出版社处理失败，暂时跳过设置publisher_id
                        }
                    }

                    bookMapper.insert(newBook);

                    // 更新采购项，关联到新创建的书籍ID
                    poi.setBookId(newBook.getBookId());
                    purchaseOrderItemMapper.updateByPoItemId(poi);
                } else {
                    // 书籍已存在，增加现有库存
                    existingBook.setStock((existingBook.getStock() == null ? 0 : existingBook.getStock()) + toAdd);
                    bookMapper.updateByBookId(existingBook);

                    // 更新采购项，关联到现有书籍ID
                    poi.setBookId(existingBook.getBookId());
                    purchaseOrderItemMapper.updateByPoItemId(poi);
                }
            }
        }
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
        // 同时设置到货时间（如果这是第一次收货）
        PurchaseOrder po = purchaseOrderMapper.selectByPoId(poi.getPoId());
        if (po != null) {
            // 检查是否这是第一次收货（到货时间为空）
            boolean isFirstReceive = (po.getExpectedArrivalDate() == null);

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

            // 如果这是第一次收货，设置到货时间为当前时间
            if (isFirstReceive) {
                po.setExpectedArrivalDate(new Date());
            }

            purchaseOrderMapper.updateByPoId(po);
        }
    }
}


