package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Book;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BookMapper {
    int deleteByBookId(Integer bookId);

    int insert(Book record);

    Book selectByBookId(Integer bookId);

    int updateByBookId(Book record);

    List<Book> selectAllByCategoryCode(String categoryCode);

    List<Book> selectByBooks(Book book);

    List<Book> selectByIsbn(String isbn);

    /**
     * Atomically decrement stock if enough inventory exists.
     * Returns number of rows affected (1 if success, 0 if not enough stock).
     */
    int decrementStockIfEnough(@Param("bookId") Integer bookId, @Param("qty") Integer qty);
}