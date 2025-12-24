package com.shop.bookshop.controller;

import java.util.List;


import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.service.BookService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;

import javax.validation.Valid;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/searchid")
    public ResultVO bookSearchByBookId(Integer bookId) {
        // Book books=bookMapper.selectByBookId(bookIde);
        Book books = bookService.bookSearchById(bookId);
        if (books != null)
            return new ResultVO(ResultCode.SUCCESS,books);
        else
            return new ResultVO(ResultCode.RECORD_NOT_FOUND, null);
    }


    @PostMapping("/delete")
    public ResultVO bookDelete(Integer bookId) {
        int books = bookService.bookDeleteSearchById(bookId);
        return new ResultVO(ResultCode.SUCCESS, null);
    }

    //添加验证注解
    @PostMapping("/insert")
    public ResultVO bookInsert(@Valid Book record) {
        int books = bookService.bookInsert(record);
        return new ResultVO(ResultCode.SUCCESS, null);
    }



    @GetMapping("/searchcode")
  public ResultVO bookSerchByCategoryCode(String catrgoryCode,Integer page,Integer limit) {
      List<Book> books = bookService.bookSearchByCode(catrgoryCode,page,limit);
        PageInfo pageInfo = new PageInfo(books);
     if(books.size()!=0)
     {
      return new ResultVO(ResultCode.SUCCESS,(int)pageInfo.getTotal(), books);
     }
     else
    	 return new ResultVO(ResultCode.RECORD_NOT_FOUND, null);

  }


    //添加验证注解
    @PostMapping("/update")
    public ResultVO bookUpdate(@Valid Book record) {
        int books = bookService.bookUpdate(record);
        return new ResultVO(ResultCode.SUCCESS, null);
    }

    /**
     * 调整库存（可传 delta 为正/负，表示增加/减少库存）
     */
    @PostMapping("/adjustStock")
    public ResultVO adjustStock(Integer bookId, Integer delta) {
        if (bookId == null || delta == null) {
            return new ResultVO<>(ResultCode.FAILED, "参数错误");
        }
        Book book = bookService.bookSearchById(bookId);
        if (book == null) {
            return new ResultVO<>(ResultCode.RECORD_NOT_FOUND, null);
        }
        Integer cur = book.getStock() == null ? 0 : book.getStock();
        int next = cur + delta;
        if (next < 0) {
            return new ResultVO<>(ResultCode.FAILED, "库存不能为负");
        }
        book.setStock(next);
        bookService.bookUpdate(book);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    /**
     * 多条件搜索  
     * @param book
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/search")
    public ResultVO searchBooks(Book book,Integer page, Integer limit) {
        if(book.getBookName().isEmpty()){
            book.setBookName(null);
        }
        if(book.getIsbn().isEmpty()){
            book.setIsbn(null);
        }
        List<Book> books = bookService.searchBooks(book, page, limit);
        PageInfo pageInfo = new PageInfo(books);
        return new ResultVO(ResultCode.SUCCESS, (int) pageInfo.getTotal(), books);
    }


}
