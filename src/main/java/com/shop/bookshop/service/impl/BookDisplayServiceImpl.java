package com.shop.bookshop.service.impl;

import com.github.pagehelper.PageHelper;
import com.shop.bookshop.dao.BookDisplayMapper;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.CategoryMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Category;
import com.shop.bookshop.service.BookDisplayService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BookDisplayServiceImpl implements BookDisplayService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private BookDisplayMapper bookDisplayMapper;


    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Book> getAllBooks(Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        List<Book> books = bookMapper.selectAllByCategoryCode(null);
        return books;
    }

    @Override
    public List<Book> getBooksByCategoryCode(Integer page, Integer limit,String categoryCode) {
        PageHelper.startPage(page,limit);
        List<Book> books = bookMapper.selectAllByCategoryCode(categoryCode);
        return books;
    }

    @Override
    public Book getBookDetailsByBookId(Integer bookId) {
        return bookMapper.selectByBookId(bookId);
    }

    @Override
    public List<Book> searchBooksByBookName(Integer page, Integer limit,String bookName) {
        PageHelper.startPage(page, limit);
        List<Book> books = bookDisplayMapper.fuzzyQueryByBookName(bookName);
        return books;
    }

    @Override
    public List<Book> searchBooks(Integer page, Integer limit, String keyword, String searchType) {
        PageHelper.startPage(page, limit);
        List<Book> books;
        switch (searchType) {
            case "isbn":
                books = bookDisplayMapper.searchByIsbn(keyword);
                break;
            case "bookName":
                books = bookDisplayMapper.searchByBookName(keyword);
                break;
            case "press":
                books = bookDisplayMapper.searchByPress(keyword);
                break;
            case "all":
            default:
                books = bookDisplayMapper.fuzzyQueryByBookName(keyword);
                break;
        }
        return books;
    }
}
