package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Keyword;
import java.util.List;

public interface KeywordMapper {
    int deleteByKeywordId(Integer keywordId);
    int insert(Keyword record);
    Keyword selectByKeywordId(Integer keywordId);
    int updateByKeywordId(Keyword record);
    List<Keyword> selectAll();
}


