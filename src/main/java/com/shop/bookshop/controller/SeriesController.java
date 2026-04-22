package com.shop.bookshop.controller;

import com.shop.bookshop.pojo.Series;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/series")
public class SeriesController {

  @Autowired private com.shop.bookshop.dao.SeriesMapper seriesMapper;

  @GetMapping("/list")
  public ResultVO<List<Series>> listSeries() {
    List<Series> list = seriesMapper.selectAll();
    return new ResultVO<>(ResultCode.SUCCESS, list.size(), list);
  }

  @PostMapping("/insert")
  public ResultVO<Integer> insertSeries(@RequestBody Series series) {
    seriesMapper.insert(series);
    return new ResultVO<>(ResultCode.SUCCESS, series.getSeriesId());
  }

  @PutMapping("/update")
  public ResultVO<Void> updateSeries(@RequestBody Series series) {
    seriesMapper.updateBySeriesId(series);
    return new ResultVO<>(ResultCode.SUCCESS, null);
  }

  @DeleteMapping("/{seriesId}")
  public ResultVO<Void> deleteSeries(@PathVariable Integer seriesId) {
    seriesMapper.deleteBySeriesId(seriesId);
    return new ResultVO<>(ResultCode.SUCCESS, null);
  }
}
