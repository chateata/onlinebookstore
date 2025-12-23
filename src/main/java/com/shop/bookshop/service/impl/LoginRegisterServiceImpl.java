package com.shop.bookshop.service.impl;

import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.LoginRegisterService;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class LoginRegisterServiceImpl implements LoginRegisterService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录处理
     * @param record
     * @param session
     */
    @Override
    public void userLogin(User record, HttpSession session) {
        User user=userMapper.selectByUserName(record.getUserName());
        if(user==null){
            //用户不存在
            throw new CustomizeException(ResultCode.USER_NOT_FOUND);
        }
        if(!user.getPassword().equals(record.getPassword())){
            //密码错误
            throw new CustomizeException(ResultCode.PASSWORD_ERROR);
        }
        session.setAttribute("user",user);
    }

    /**
     * 用户注册处理
     * @param record
     */
    @Override
    public void userRegister(User record) {
        User user=userMapper.selectByUserName(record.getUserName());
        if(user!=null){
            throw new CustomizeException(ResultCode.FAILED,"用户名已存在");
        }
        // 设置默认信用等级为3和默认余额100（若未指定）
        if (record.getCreditLevelId() == null) {
            record.setCreditLevelId(3);
        }
        if (record.getAccountBalance() == null) {
            record.setAccountBalance(new java.math.BigDecimal("100.00"));
        }
        userMapper.insert(record);
    }

    @Override
    public void adminLogin(Admin admin, HttpSession session) {

    }
}
