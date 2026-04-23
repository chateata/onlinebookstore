package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.service.BookService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookControllerUnitTest {

  private BookController controller;

  @Mock private BookService bookService;

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
    controller = new BookController();
    setField(controller, "bookService", bookService);
  }

  @Test
  void bookSearchByBookId_whenFound_shouldReturnSuccess() {
    Book b = new Book();
    b.setBookId(1);
    when(bookService.bookSearchById(1)).thenReturn(b);

    ResultVO r = controller.bookSearchByBookId(1);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(b, r.getData());
  }

  @Test
  void bookSearchByBookId_whenNotFound_shouldReturnRecordNotFound() {
    when(bookService.bookSearchById(1)).thenReturn(null);

    ResultVO r = controller.bookSearchByBookId(1);

    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r.getCode());
  }

  @Test
  void bookSerchByCategoryCode_whenEmpty_shouldReturnRecordNotFound() {
    when(bookService.bookSearchByCode(eq("C1"), eq(1), eq(10))).thenReturn(Collections.emptyList());

    ResultVO r = controller.bookSerchByCategoryCode("C1", 1, 10);

    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r.getCode());
  }

  @Test
  void bookSerchByCategoryCode_whenNonEmpty_shouldReturnSuccessWithTotal() {
    Book b = new Book();
    b.setBookId(1);
    List<Book> list = Collections.singletonList(b);
    when(bookService.bookSearchByCode(eq("C1"), eq(1), eq(10))).thenReturn(list);

    ResultVO r = controller.bookSerchByCategoryCode("C1", 1, 10);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(1, r.getCount());
    assertEquals(list, r.getData());
  }

  @Test
  void adjustStock_whenParamsNull_shouldReturnFailed() {
    ResultVO r1 = controller.adjustStock(null, 1);
    assertEquals(ResultCode.FAILED.getCode(), r1.getCode());

    ResultVO r2 = controller.adjustStock(1, null);
    assertEquals(ResultCode.FAILED.getCode(), r2.getCode());
  }

  @Test
  void adjustStock_whenBookMissing_shouldReturnRecordNotFound() {
    when(bookService.bookSearchById(1)).thenReturn(null);

    ResultVO r = controller.adjustStock(1, 1);

    assertEquals(ResultCode.RECORD_NOT_FOUND.getCode(), r.getCode());
    verify(bookService, never()).bookUpdate(any(Book.class));
  }

  @Test
  void adjustStock_whenWouldGoNegative_shouldReturnFailed() {
    Book b = new Book();
    b.setBookId(1);
    b.setStock(1);
    when(bookService.bookSearchById(1)).thenReturn(b);

    ResultVO r = controller.adjustStock(1, -2);

    assertEquals(ResultCode.FAILED.getCode(), r.getCode());
    verify(bookService, never()).bookUpdate(any(Book.class));
  }

  @Test
  void adjustStock_whenOk_shouldUpdateAndReturnSuccess() {
    Book b = new Book();
    b.setBookId(1);
    b.setStock(null);
    when(bookService.bookSearchById(1)).thenReturn(b);

    ResultVO r = controller.adjustStock(1, 2);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    verify(bookService).bookUpdate(b);
    assertEquals(Integer.valueOf(2), b.getStock());
  }

  @Test
  void searchBooks_shouldNullOutEmptyStrings_andReturnSuccess() {
    Book q = new Book();
    q.setBookName("");
    q.setIsbn("");
    q.setAuthor("a");
    q.setPress("p");
    q.setPrice(new BigDecimal("1.00"));
    q.setStock(1);

    when(bookService.searchBooks(any(Book.class), eq(1), eq(10))).thenReturn(Collections.emptyList());

    ResultVO r = controller.searchBooks(q, 1, 10);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());

    ArgumentCaptor<Book> cap = ArgumentCaptor.forClass(Book.class);
    verify(bookService).searchBooks(cap.capture(), eq(1), eq(10));
    assertEquals(null, cap.getValue().getBookName());
    assertEquals(null, cap.getValue().getIsbn());
  }

  @Test
  void bookDelete_bookInsert_bookUpdate_shouldReturnSuccess() {
    ResultVO d = controller.bookDelete(1);
    assertEquals(ResultCode.SUCCESS.getCode(), d.getCode());

    ResultVO i = controller.bookInsert(new Book());
    assertEquals(ResultCode.SUCCESS.getCode(), i.getCode());

    ResultVO u = controller.bookUpdate(new Book());
    assertEquals(ResultCode.SUCCESS.getCode(), u.getCode());
  }
}

