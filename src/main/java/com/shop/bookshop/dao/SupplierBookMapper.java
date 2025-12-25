package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.SupplierBook;
import java.util.List;

public interface SupplierBookMapper {
    int deleteBySupplierBookId(Integer supplierBookId);
    int insert(SupplierBook record);
    SupplierBook selectBySupplierBookId(Integer supplierBookId);
    int updateBySupplierBookId(SupplierBook record);
    List<SupplierBook> selectAll();
    List<SupplierBook> selectBySupplierId(Integer supplierId);
    List<SupplierBook> selectByIsbn(String isbn);
    List<SupplierBook> selectByTitle(String title);
}

