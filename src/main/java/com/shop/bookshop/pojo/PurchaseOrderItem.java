package com.shop.bookshop.pojo;

import java.math.BigDecimal;

public class PurchaseOrderItem {
    private Integer poItemId;
    private Integer poId;
    private Integer bookId;
    private Integer supplierBookId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer shortageId;
    private Integer receivedQuantity;
    private Book book;
    private SupplierBook supplierBook;

    public Integer getPoItemId() {
        return poItemId;
    }

    public void setPoItemId(Integer poItemId) {
        this.poItemId = poItemId;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getSupplierBookId() {
        return supplierBookId;
    }

    public void setSupplierBookId(Integer supplierBookId) {
        this.supplierBookId = supplierBookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getShortageId() {
        return shortageId;
    }

    public void setShortageId(Integer shortageId) {
        this.shortageId = shortageId;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public SupplierBook getSupplierBook() {
        return supplierBook;
    }

    public void setSupplierBook(SupplierBook supplierBook) {
        this.supplierBook = supplierBook;
    }
}


