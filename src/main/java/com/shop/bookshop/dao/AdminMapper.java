package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.Admin;

public interface AdminMapper {
    Admin selectByAdminName(String adminName);
    int insert(Admin admin);
}




