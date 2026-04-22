package com.shop.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.bookshop.dao.SeriesMapper;
import com.shop.bookshop.pojo.Series;
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
public class SeriesControllerUnitTest {

  private SeriesController controller;
  @Mock private SeriesMapper seriesMapper;

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
    controller = new SeriesController();
    setField(controller, "seriesMapper", seriesMapper);

    Series s1 = new Series();
    s1.setSeriesId(1);
    Series s2 = new Series();
    s2.setSeriesId(2);
    List<Series> list = Arrays.asList(s1, s2);
    when(seriesMapper.selectAll()).thenReturn(list);

    ResultVO<List<Series>> vo = controller.listSeries();

    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(2, vo.getCount());
    assertNotNull(vo.getData());
    assertEquals(2, vo.getData().size());
  }

  @Test
  void insert_shouldReturnSeriesId() {
    controller = new SeriesController();
    setField(controller, "seriesMapper", seriesMapper);

    Series input = new Series();
    input.setSeriesId(101);
    input.setSeriesName("S");
    when(seriesMapper.insert(any(Series.class))).thenReturn(1);

    ResultVO<Integer> vo = controller.insertSeries(input);

    verify(seriesMapper).insert(any(Series.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
    assertEquals(Integer.valueOf(101), vo.getData());
  }

  @Test
  void update_shouldReturnSuccess() {
    controller = new SeriesController();
    setField(controller, "seriesMapper", seriesMapper);

    Series input = new Series();
    input.setSeriesId(101);
    input.setSeriesName("S2");
    when(seriesMapper.updateBySeriesId(any(Series.class))).thenReturn(1);

    ResultVO<Void> vo = controller.updateSeries(input);

    verify(seriesMapper).updateBySeriesId(any(Series.class));
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }

  @Test
  void delete_shouldReturnSuccess() {
    controller = new SeriesController();
    setField(controller, "seriesMapper", seriesMapper);

    when(seriesMapper.deleteBySeriesId(101)).thenReturn(1);

    ResultVO<Void> vo = controller.deleteSeries(101);

    verify(seriesMapper).deleteBySeriesId(101);
    assertEquals(ResultCode.SUCCESS.getCode(), vo.getCode());
  }
}

