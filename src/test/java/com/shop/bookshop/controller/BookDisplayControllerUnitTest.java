package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Category;
import com.shop.bookshop.service.BookDisplayService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
public class BookDisplayControllerUnitTest {

  private BookDisplayController controller;

  @Mock private BookDisplayService bookDisplayService;
  @Mock private Model model;

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
    controller = new BookDisplayController();
    setField(controller, "bookDisplayService", bookDisplayService);
  }

  @Test
  void getCategories_shouldReturnSuccess() {
    when(bookDisplayService.getAllCategories()).thenReturn(Collections.singletonList(new Category()));
    ResultVO r = controller.getCategories();
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
  }

  @Test
  void getBooksByCategoryCode_shouldDefaultPageLimit() {
    when(bookDisplayService.getBooksByCategoryCode(eq(1), eq(10), eq("C1")))
        .thenReturn(Collections.emptyList());

    ResultVO r = controller.getBooksByCategoryCode("C1", null, null);
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(0, r.getCount());
    verify(bookDisplayService).getBooksByCategoryCode(1, 10, "C1");
  }

  @Test
  void bookDetailsView_shouldAddModelAttribute_andReturnViewName() {
    Book b = new Book();
    b.setBookId(1);
    when(bookDisplayService.getBookDetailsByBookId(1)).thenReturn(b);

    String view = controller.bookDetailsView(1, model);

    assertEquals("details", view);
    verify(model).addAttribute("book", b);
  }

  @Test
  void searchBook_shouldDelegateAndReturnSuccess() {
    when(bookDisplayService.searchBooks(eq(1), eq(10), eq("k"), eq("all")))
        .thenReturn(Collections.emptyList());

    ResultVO r = controller.searchBook("k", "all");
    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals(0, r.getCount());
  }
}

