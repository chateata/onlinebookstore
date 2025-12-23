package com.shop.bookshop.dao;

import com.shop.bookshop.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelectByUserName(){
        User user =userMapper.selectByUserName("张三d");
        assertEquals(null,user);

    }
}
