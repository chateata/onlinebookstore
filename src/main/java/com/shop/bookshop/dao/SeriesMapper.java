package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Series;
import java.util.List;

public interface SeriesMapper {
    int deleteBySeriesId(Integer seriesId);
    int insert(Series record);
    Series selectBySeriesId(Integer seriesId);
    int updateBySeriesId(Series record);
    List<Series> selectAll();
}


