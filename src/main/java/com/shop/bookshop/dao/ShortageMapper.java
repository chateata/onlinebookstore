package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Shortage;
import java.util.List;

public interface ShortageMapper {
    int deleteByShortageId(Integer shortageId);
    int insert(Shortage record);
    Shortage selectByShortageId(Integer shortageId);
    int updateByShortageId(Shortage record);
    List<Shortage> selectUnprocessed();
}


