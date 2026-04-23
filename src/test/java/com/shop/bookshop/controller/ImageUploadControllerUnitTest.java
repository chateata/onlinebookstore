package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ImageUploadControllerUnitTest {

  private ImageUploadController controller;

  @Mock private MultipartFile bookImage;
  @Mock private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    controller = new ImageUploadController();
  }

  @Test
  void uploadBookImage_whenOk_shouldReturnSuccessWithFileName() throws Exception {
    org.mockito.Mockito.when(bookImage.getOriginalFilename()).thenReturn("a.png");

    ResultVO r = controller.uploadBookImage(bookImage, request);

    assertEquals(ResultCode.SUCCESS.getCode(), r.getCode());
    assertEquals("a.png", r.getData());
  }

  @Test
  void uploadBookImage_whenTransferFails_shouldThrowCustomizeException() throws Exception {
    org.mockito.Mockito.when(bookImage.getOriginalFilename()).thenReturn("a.png");
    org.mockito.Mockito.doThrow(new IOException("x"))
        .when(bookImage)
        .transferTo(org.mockito.ArgumentMatchers.any(java.io.File.class));

    CustomizeException ex =
        assertThrows(CustomizeException.class, () -> controller.uploadBookImage(bookImage, request));
    assertEquals(ResultCode.FAILED.getCode(), ex.getCode());
  }
}

