package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.CreditLevel;
import java.util.List;

public interface CreditLevelMapper {
    int deleteByLevelId(Integer levelId);
    int insert(CreditLevel record);
    CreditLevel selectByLevelId(Integer levelId);
    int updateByLevelId(CreditLevel record);
    List<CreditLevel> selectAll();
}


