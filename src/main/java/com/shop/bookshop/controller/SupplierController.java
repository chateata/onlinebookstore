package com.shop.bookshop.controller;

import com.github.pagehelper.PageInfo;
import com.shop.bookshop.pojo.Supplier;
import com.shop.bookshop.pojo.SupplierBook;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {
    @Autowired
    private com.shop.bookshop.dao.SupplierMapper supplierMapper;
    @Autowired
    private com.shop.bookshop.dao.SupplierBookMapper supplierBookMapper;

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

    // ========== 供应商书目管理 ==========

    @GetMapping("/book/list")
    public ResultVO<List<SupplierBook>> listSupplierBooks(@RequestParam(required = false) Integer supplierId) {
        List<SupplierBook> list;
        if (supplierId != null) {
            list = supplierBookMapper.selectBySupplierId(supplierId);
        } else {
            list = supplierBookMapper.selectAll();
        }
        PageInfo<SupplierBook> pageInfo = new PageInfo<>(list);
        return new ResultVO<>(ResultCode.SUCCESS, (int)pageInfo.getTotal(), list);
    }

    @PostMapping("/book/insert")
    public ResultVO<Void> insertSupplierBook(@RequestBody SupplierBook supplierBook) {
        if (supplierBook.getStatus() == null) {
            supplierBook.setStatus("ACTIVE");
        }
        if (supplierBook.getCreateTime() == null) {
            supplierBook.setCreateTime(new Date());
        }
        if (supplierBook.getUpdateTime() == null) {
            supplierBook.setUpdateTime(new Date());
        }
        supplierBookMapper.insert(supplierBook);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    @PutMapping("/book/update")
    public ResultVO<Void> updateSupplierBook(@RequestBody SupplierBook supplierBook) {
        if (supplierBook.getUpdateTime() == null) {
            supplierBook.setUpdateTime(new Date());
        }
        supplierBookMapper.updateBySupplierBookId(supplierBook);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    @DeleteMapping("/book/{supplierBookId}")
    public ResultVO<Void> deleteSupplierBook(@PathVariable Integer supplierBookId) {
        supplierBookMapper.deleteBySupplierBookId(supplierBookId);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    @GetMapping("/book/search")
    public ResultVO<List<SupplierBook>> searchSupplierBooks(@RequestParam(required = false) String isbn,
                                                           @RequestParam(required = false) String title,
                                                           @RequestParam(required = false) Integer supplierId) {
        List<SupplierBook> list;
        if (isbn != null && !isbn.trim().isEmpty()) {
            list = supplierBookMapper.selectByIsbn(isbn.trim());
        } else if (title != null && !title.trim().isEmpty()) {
            list = supplierBookMapper.selectByTitle(title.trim());
        } else if (supplierId != null) {
            list = supplierBookMapper.selectBySupplierId(supplierId);
        } else {
            list = supplierBookMapper.selectAll();
        }
        PageInfo<SupplierBook> pageInfo = new PageInfo<>(list);
        return new ResultVO<>(ResultCode.SUCCESS, (int)pageInfo.getTotal(), list);
    }
}


