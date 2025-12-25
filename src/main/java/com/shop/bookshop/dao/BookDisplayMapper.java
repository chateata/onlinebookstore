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

    /**
     * 根据ISBN精确查询
     * @param isbn
     * @return
     */
    List<Book> searchByIsbn(String isbn);

    /**
     * 根据书名模糊查询
     * @param bookName
     * @return
     */
    List<Book> searchByBookName(String bookName);

    /**
     * 根据出版社模糊查询
     * @param press
     * @return
     */
    List<Book> searchByPress(String press);
}
