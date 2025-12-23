package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Supplier;
import java.util.List;

public interface SupplierMapper {
    int deleteBySupplierId(Integer supplierId);
    int insert(Supplier record);
    Supplier selectBySupplierId(Integer supplierId);
    int updateBySupplierId(Supplier record);
    List<Supplier> selectAll();
}


