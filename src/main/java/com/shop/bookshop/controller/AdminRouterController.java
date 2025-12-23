package com.shop.bookshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminRouterController {

    //用户管理页面
    @GetMapping("/user_manage")
    public String toUserManage(){
        return "admin/user";
    }

    //书籍管理页面
    @GetMapping({"/","/book_manage"})
    public String toBookManage(){
        return "admin/books";
    }

    //分类管理页面
    @GetMapping("/category_manage")
    public String toCategoryManage(){
        return "admin/category";
    }

    //订单管理页面
    @GetMapping("/order_manage")
    public String toOrderManage(){
        return "admin/order";
    }

    //添加书籍页面
    @GetMapping("/add_book")
    public String AddBook(){
        return "admin/add_book";
    }

    //后台登录页面
    @GetMapping("/login")
    public String toAdminLogin(){
        return "admin/login";
    }

}
