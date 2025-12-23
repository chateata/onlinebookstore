package com.shop.bookshop.pojo;

import java.util.Date;

public class Shortage {
    private Integer shortageId;
    private Integer bookId;
    private Integer quantity;
    private Date registerDate;
    private String source;
    private Boolean isProcessed;
    private Integer customerRequestId;

    public Integer getShortageId() {
        return shortageId;
    }

    public void setShortageId(Integer shortageId) {
        this.shortageId = shortageId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Integer getCustomerRequestId() {
        return customerRequestId;
    }

    public void setCustomerRequestId(Integer customerRequestId) {
        this.customerRequestId = customerRequestId;
    }
}


