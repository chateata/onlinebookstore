package com.shop.bookshop.controller;

import com.shop.bookshop.dao.CreditLevelMapper;
import com.shop.bookshop.pojo.CreditLevel;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/creditLevel")
public class CreditLevelController {

    @Autowired
    private CreditLevelMapper creditLevelMapper;

    /**
     * 获取所有信用等级列表
     * @return
     */
    @GetMapping("/list")
    public ResultVO<List<CreditLevel>> getCreditLevelList() {
        List<CreditLevel> creditLevels = creditLevelMapper.selectAll();
        return new ResultVO<List<CreditLevel>>(ResultCode.SUCCESS, creditLevels.size(), creditLevels);
    }
}
