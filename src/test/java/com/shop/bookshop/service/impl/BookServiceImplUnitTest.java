package com.shop.bookshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.PublisherMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Publisher;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplUnitTest {

  private BookServiceImpl service;

  @Mock private BookMapper bookMapper;
  @Mock private PublisherMapper publisherMapper;

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
    service = new BookServiceImpl();
    setField(service, "bookMapper", bookMapper);
    setField(service, "publisherMapper", publisherMapper);
  }

  @Test
  void bookSearchById_whenFound_shouldReturnBook() {
    Book b = new Book();
    b.setBookId(1);
    when(bookMapper.selectByBookId(1)).thenReturn(b);

    Book got = service.bookSearchById(1);

    assertEquals(Integer.valueOf(1), got.getBookId());
    verify(bookMapper).selectByBookId(1);
  }

  @Test
  void bookSearchById_whenNotFound_shouldReturnNull() {
    when(bookMapper.selectByBookId(999)).thenReturn(null);

    Book got = service.bookSearchById(999);

    assertNull(got);
    verify(bookMapper).selectByBookId(999);
  }

  @Test
  void bookSearchByCode_shouldDelegateToMapperWithCategoryCode() {
    List<Book> list = Arrays.asList(new Book(), new Book());
    when(bookMapper.selectAllByCategoryCode("C1")).thenReturn(list);

    List<Book> got = service.bookSearchByCode("C1", 1, 10);

    assertEquals(2, got.size());
    verify(bookMapper).selectAllByCategoryCode("C1");
  }

  @Test
  void bookDeleteSearchById_shouldReturnMapperResult() {
    when(bookMapper.deleteByBookId(1)).thenReturn(1);
    when(bookMapper.deleteByBookId(999)).thenReturn(0);

    assertEquals(1, service.bookDeleteSearchById(1));
    assertEquals(0, service.bookDeleteSearchById(999));
  }

  @Test
  void bookUpdate_shouldReturnMapperResult() {
    Book record = new Book();
    record.setBookId(1);
    when(bookMapper.updateByBookId(record)).thenReturn(1);

    int updated = service.bookUpdate(record);

    assertEquals(1, updated);
    verify(bookMapper).updateByBookId(record);
  }

  @Test
  void searchBooks_shouldDelegateToMapper() {
    Book criteria = new Book();
    criteria.setIsbn("x");
    when(bookMapper.selectByBooks(criteria)).thenReturn(Collections.singletonList(new Book()));

    List<Book> got = service.searchBooks(criteria, 1, 10);

    assertEquals(1, got.size());
    verify(bookMapper).selectByBooks(criteria);
  }

  @Test
  void bookInsert_shouldSetCreateTimeAndDefaultCategory_whenMissing_andSetExistingPublisherId() {
    Book record = new Book();
    record.setPress("PressA");
    record.setCategoryCode(null);
    record.setCreateTime(null);

    Publisher existing = new Publisher();
    existing.setPublisherId(88);
    when(publisherMapper.selectByName("PressA")).thenReturn(existing);
    when(bookMapper.insert(record)).thenReturn(1);

    int inserted = service.bookInsert(record);

    assertEquals(1, inserted);
    assertNotNull(record.getCreateTime());
    assertEquals("default", record.getCategoryCode());
    assertEquals(Integer.valueOf(88), record.getPublisherId());
    verify(publisherMapper).selectByName("PressA");
    verify(publisherMapper, never()).insert(any(Publisher.class));
    verify(bookMapper).insert(record);
  }

  @Test
  void bookInsert_shouldCreatePublisher_whenNotExists_andUseNewPublisherId() {
    Book record = new Book();
    record.setPress("NewPress");
    record.setCategoryCode("C1");
    record.setCreateTime(new Date());

    when(publisherMapper.selectByName("NewPress")).thenReturn(null);
    when(publisherMapper.insert(any(Publisher.class)))
        .thenAnswer(
            inv -> {
              Publisher p = inv.getArgument(0);
              p.setPublisherId(101);
              return 1;
            });
    when(bookMapper.insert(record)).thenReturn(1);

    int inserted = service.bookInsert(record);

    assertEquals(1, inserted);
    assertEquals(Integer.valueOf(101), record.getPublisherId());
    verify(publisherMapper).selectByName("NewPress");
    ArgumentCaptor<Publisher> captor = ArgumentCaptor.forClass(Publisher.class);
    verify(publisherMapper).insert(captor.capture());
    assertEquals("NewPress", captor.getValue().getName());
    verify(bookMapper).insert(record);
  }

  @Test
  void bookInsert_whenPressBlank_shouldSkipPublisherLookup() {
    Book record = new Book();
    record.setPress("   ");
    record.setCategoryCode("   ");
    record.setCreateTime(null);
    when(bookMapper.insert(record)).thenReturn(1);

    int inserted = service.bookInsert(record);

    assertEquals(1, inserted);
    assertNotNull(record.getCreateTime());
    assertEquals("default", record.getCategoryCode());
    verify(publisherMapper, never()).selectByName(any(String.class));
    verify(publisherMapper, never()).insert(any(Publisher.class));
    verify(bookMapper).insert(record);
  }

  @Test
  void bookInsert_whenPublisherLookupThrows_shouldStillInsertBook() {
    Book record = new Book();
    record.setPress("PressA");
    record.setCategoryCode("C1");
    record.setCreateTime(new Date());

    when(publisherMapper.selectByName("PressA")).thenThrow(new RuntimeException("boom"));
    when(bookMapper.insert(record)).thenReturn(1);

    int inserted = service.bookInsert(record);

    assertEquals(1, inserted);
    verify(publisherMapper).selectByName("PressA");
    verify(bookMapper).insert(record);
  }
}

