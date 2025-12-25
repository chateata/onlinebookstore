package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Admin;
import java.util.List;

public interface AdminMapper {
    Admin selectByAdminName(String adminName);
    List<Admin> selectAll();
    int insert(Admin admin);
    int updateByAdminName(Admin admin);
    int deleteByAdminName(String adminName);
}




