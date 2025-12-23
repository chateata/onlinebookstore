package com.shop.bookshop.service;

import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Category;

import java.util.List;

public interface BookDisplayService {

    List<Category> getAllCategories();

    List<Book> getAllBooks(Integer page,Integer limit);

    List<Book> getBooksByCategoryCode(Integer page, Integer limit,String categoryCode);

    Book getBookDetailsByBookId(Integer bookId);

    List<Book> searchBooksByBookName(Integer page, Integer limit,String bookName);
}

