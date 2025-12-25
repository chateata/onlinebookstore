package com.shop.bookshop.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class SupplierBook {
    private Integer supplierBookId;
    private Integer supplierId;
    private Integer seriesId;
    private String isbn;
    private String title;
    private String author;
    private String press;
    private BigDecimal price;
    private BigDecimal supplyPrice;
    private String description;
    private String status;
    private Date createTime;
    private Date updateTime;

    // 关联对象
    private Supplier supplier;
    private com.shop.bookshop.pojo.Series series;

    public Integer getSupplierBookId() {
        return supplierBookId;
    }

    public void setSupplierBookId(Integer supplierBookId) {
        this.supplierBookId = supplierBookId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public com.shop.bookshop.pojo.Series getSeries() {
        return series;
    }

    public void setSeries(com.shop.bookshop.pojo.Series series) {
        this.series = series;
    }
}

