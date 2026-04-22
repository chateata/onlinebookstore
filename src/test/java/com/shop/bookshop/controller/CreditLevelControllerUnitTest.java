package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.CreditLevelMapper;
import com.shop.bookshop.pojo.CreditLevel;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreditLevelControllerUnitTest {

  private CreditLevelController controller;
  @Mock private CreditLevelMapper creditLevelMapper;

  private static void setField(Object target, String name, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to set field: " + name, e);
    }
  }

  @Test
  void list_shouldReturnSuccessAndCount() {
    controller = new CreditLevelController();
    setField(controller, "creditLevelMapper", creditLevelMapper);

    CreditLevel a = new CreditLevel();
    a.setLevelId(1);
    CreditLevel b = new CreditLevel();
    b.setLevelId(2);
    List<CreditLevel> list = Arrays.asList(a, b);
    when(creditLevelMapper.selectAll()).thenReturn(list);

    ResultVO<List<CreditLevel>> vo = controller.getCreditLevelList();

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(2, vo.getCount());
    assertNotNull(vo.getData());
    assertEquals(2, vo.getData().size());
  }
}

