package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByByCategoryCode(String categoryCode);

    int insert(Category record);

    Category selectByByCategoryCode(String categoryCode);

    int updateByCategoryCode(Category record);

    List<Category> selectAll();
}