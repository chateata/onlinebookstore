package com.shop.bookshop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.PublisherMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Publisher;
import com.shop.bookshop.service.BookService;

@Service
public class BookServiceImpl implements BookService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private PublisherMapper publisherMapper;
	@Override
	public Book bookSearchById(Integer bookId) {
		// TODO Auto-generated method stub
		Book books=bookMapper.selectByBookId(bookId);
		return books;
	}

	//(修改成分页查询)
	@Override
	public List<Book> bookSearchByCode(String catrgoryCode,Integer page,Integer limit) {
			PageHelper.startPage(page, limit);
	      List<Book> books = bookMapper.selectAllByCategoryCode(catrgoryCode);
	   
	      return books;
		// TODO Auto-generated method stub

	}

	@Override
	public int bookDeleteSearchById(Integer bookId) {
		// TODO Auto-generated method stub
		int books=bookMapper.deleteByBookId(bookId);
		return books;
	}

	@Override
	public int bookInsert(Book record) {
		// 设置创建时间
		if (record.getCreateTime() == null) {
			record.setCreateTime(new java.util.Date());
		}

		// 设置默认分类
		if (record.getCategoryCode() == null || record.getCategoryCode().trim().isEmpty()) {
			record.setCategoryCode("default");
		}

		// 处理出版社信息
		if (record.getPress() != null && !record.getPress().trim().isEmpty()) {
			try {
				// 查找是否已存在相同名称的出版社
				Publisher existingPublisher = publisherMapper.selectByName(record.getPress());
				if (existingPublisher == null) {
					// 创建新出版社
					Publisher newPublisher = new Publisher();
					newPublisher.setName(record.getPress());
					publisherMapper.insert(newPublisher);
					record.setPublisherId(newPublisher.getPublisherId());
				} else {
					record.setPublisherId(existingPublisher.getPublisherId());
				}
			} catch (Exception e) {
				// 如果出版社处理失败，暂时跳过设置publisher_id
			}
		}

		int books=bookMapper.insert(record);
                return books;
	}

	@Override
	public int bookUpdate(Book record) {
		// TODO Auto-generated method stub
		int books=bookMapper.updateByBookId(record);
		return books;

	}


	/**
	 * 多条件查询书籍 
	 * @param book
	 * @return
	 */
	@Override
	public List<Book> searchBooks(Book book, Integer page, Integer limit) {
		PageHelper.startPage(page,limit);
		List<Book> books = bookMapper.selectByBooks(book);
		return books;
	}
}
