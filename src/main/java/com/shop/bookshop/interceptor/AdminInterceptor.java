package com.shop.bookshop.interceptor;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.util.ResultCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("admin")==null) {
            //用户未登录,抛出异常
            throw new CustomizeException(ResultCode.USER_NOT_LOGGED_IN);
        }
        Admin admin = (Admin) session.getAttribute("admin");
        //已登录，放行请求
        return true;
    }
}
