package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Publisher;
import java.util.List;

public interface PublisherMapper {
    int deleteByPublisherId(Integer publisherId);
    int insert(Publisher record);
    Publisher selectByPublisherId(Integer publisherId);
    Publisher selectByName(String name);
    int updateByPublisherId(Publisher record);
    List<Publisher> selectAll();
}


