package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Book;

import java.util.List;

public interface BookDisplayMapper {

    /**
     * 根据书名模糊查询
     * @param bookName
     * @return
     */
    List<Book> fuzzyQueryByBookName(String bookName);
}
