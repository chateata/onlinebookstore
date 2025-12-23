package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Author;
import java.util.List;

public interface AuthorMapper {
    int deleteByAuthorId(Integer authorId);
    int insert(Author record);
    Author selectByAuthorId(Integer authorId);
    int updateByAuthorId(Author record);
    List<Author> selectAll();
}


