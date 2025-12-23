package com.shop.bookshop.service;

import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.pojo.User;

import javax.servlet.http.HttpSession;

public interface LoginRegisterService {

    /**
     * 用户登录
     * @param record
     * @param session
     */
    void userLogin(User record, HttpSession session);

    /**
     * 用户注册
     * @param record
     */
    void userRegister(User record);

    /**
     * 管理员登录
     * @param admin
     * @param session
     */
    void adminLogin(Admin admin, HttpSession session);
}
