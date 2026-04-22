package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.BookDisplayMapper;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.CategoryMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Category;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookDisplayServiceImplUnitTest {

  private BookDisplayServiceImpl service;

  @Mock private BookMapper bookMapper;
  @Mock private CategoryMapper categoryMapper;
  @Mock private BookDisplayMapper bookDisplayMapper;

  private static void setField(Object target, String name, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to set field: " + name, e);
    }
  }

  @BeforeEach
  void setUp() {
    service = new BookDisplayServiceImpl();
    setField(service, "bookMapper", bookMapper);
    setField(service, "categoryMapper", categoryMapper);
    setField(service, "bookDisplayMapper", bookDisplayMapper);
  }

  @Test
  void getAllCategories_shouldDelegateToMapper() {
    List<Category> categories = Arrays.asList(new Category(), new Category());
    when(categoryMapper.selectAll()).thenReturn(categories);

    List<Category> result = service.getAllCategories();

    assertSame(categories, result);
    verify(categoryMapper).selectAll();
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void getAllBooks_shouldQueryAllBooksWithNullCategoryCode() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookMapper.selectAllByCategoryCode(null)).thenReturn(books);

    List<Book> result = service.getAllBooks(1, 10);

    assertSame(books, result);
    verify(bookMapper).selectAllByCategoryCode(null);
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void getBooksByCategoryCode_shouldDelegateCategoryCodeToMapper() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookMapper.selectAllByCategoryCode("C1")).thenReturn(books);

    List<Book> result = service.getBooksByCategoryCode(1, 10, "C1");

    assertSame(books, result);
    verify(bookMapper).selectAllByCategoryCode("C1");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void getBookDetailsByBookId_shouldDelegateToMapper() {
    Book book = new Book();
    when(bookMapper.selectByBookId(1)).thenReturn(book);

    Book result = service.getBookDetailsByBookId(1);

    assertSame(book, result);
    verify(bookMapper).selectByBookId(1);
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooksByBookName_shouldUseFuzzyQuery() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.fuzzyQueryByBookName("java")).thenReturn(books);

    List<Book> result = service.searchBooksByBookName(1, 10, "java");

    assertSame(books, result);
    verify(bookDisplayMapper).fuzzyQueryByBookName("java");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooks_whenSearchTypeIsbn_shouldCallSearchByIsbn() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.searchByIsbn("978")).thenReturn(books);

    List<Book> result = service.searchBooks(1, 10, "978", "isbn");

    assertSame(books, result);
    verify(bookDisplayMapper).searchByIsbn("978");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooks_whenSearchTypeBookName_shouldCallSearchByBookName() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.searchByBookName("Java")).thenReturn(books);

    List<Book> result = service.searchBooks(1, 10, "Java", "bookName");

    assertSame(books, result);
    verify(bookDisplayMapper).searchByBookName("Java");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooks_whenSearchTypePress_shouldCallSearchByPress() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.searchByPress("press")).thenReturn(books);

    List<Book> result = service.searchBooks(1, 10, "press", "press");

    assertSame(books, result);
    verify(bookDisplayMapper).searchByPress("press");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooks_whenSearchTypeAll_shouldCallFuzzyQuery() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.fuzzyQueryByBookName("k")).thenReturn(books);

    List<Book> result = service.searchBooks(1, 10, "k", "all");

    assertSame(books, result);
    verify(bookDisplayMapper).fuzzyQueryByBookName("k");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }

  @Test
  void searchBooks_whenSearchTypeUnknown_shouldDefaultToFuzzyQuery() {
    List<Book> books = Collections.singletonList(new Book());
    when(bookDisplayMapper.fuzzyQueryByBookName("k")).thenReturn(books);

    List<Book> result = service.searchBooks(1, 10, "k", "unknown");

    assertSame(books, result);
    verify(bookDisplayMapper).fuzzyQueryByBookName("k");
    verifyNoMoreInteractions(categoryMapper, bookMapper, bookDisplayMapper);
  }
}

