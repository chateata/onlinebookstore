package com.shop.bookshop.controller;

import com.github.pagehelper.PageInfo;
import com.shop.bookshop.pojo.Supplier;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private com.shop.bookshop.dao.SupplierMapper supplierMapper;

    @GetMapping("/list")
    public ResultVO<List<Supplier>> listSuppliers() {
        List<Supplier> list = supplierMapper.selectAll();
        PageInfo<Supplier> pageInfo = new PageInfo<>(list);
        return new ResultVO<>(ResultCode.SUCCESS, (int)pageInfo.getTotal(), list);
    }

    @PostMapping("/insert")
    public ResultVO<Void> insertSupplier(@RequestBody Supplier supplier) {
        supplierMapper.insert(supplier);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    @PutMapping("/update")
    public ResultVO<Void> updateSupplier(@RequestBody Supplier supplier) {
        supplierMapper.updateBySupplierId(supplier);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    @DeleteMapping("/{supplierId}")
    public ResultVO<Void> deleteSupplier(@PathVariable Integer supplierId) {
        supplierMapper.deleteBySupplierId(supplierId);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }
}


