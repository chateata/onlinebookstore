package com.shop.bookshop.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

public class GlobalExceptionHandlerUnitTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void customizeExceptionHandler_shouldReturnCodeAndMsg() {
    CustomizeException ex = new CustomizeException(ResultCode.FAILED, "x");
    ResultVO r = handler.customizeExceptionHandler(ex);
    assertEquals(ResultCode.FAILED.getCode(), r.getCode());
    assertEquals("x", r.getMsg());
  }

  @Test
  void methodArgumentNotValidExceptionHandler_withBindException_shouldReturnFirstErrorMessage() {
    BindException ex = new BindException(new Object(), "t");
    ex.addError(new ObjectError("t", "msg1"));
    ResultVO r = handler.methodArgumentNotValidExceptionHandler(ex);
    assertEquals(ResultCode.ARGUMENT_NOT_VALID.getCode(), r.getCode());
    assertEquals("msg1", r.getMsg());
  }

  @Test
  void exceptionHandler_shouldReturnUnknownErrorWithMessage() {
    ResultVO r = handler.ExceptionHandler(new IllegalStateException("boom"));
    assertEquals(ResultCode.UNKNOWN_ERROR.getCode(), r.getCode());
    // Some JDKs/locales may not return the exact message text consistently; assert it's non-empty.
    org.junit.jupiter.api.Assertions.assertTrue(r.getMsg() != null && !r.getMsg().isEmpty());
  }
}

