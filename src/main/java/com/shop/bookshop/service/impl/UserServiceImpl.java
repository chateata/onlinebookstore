package com.shop.bookshop.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.UserService;
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
	@Override
	public int deleteByUserId(Integer userId) {
		// TODO Auto-generated method stub
		int users= userMapper.deleteByUserId(userId);
		return users;
	}

	@Override
	public int insert(User record) {
		// TODO Auto-generated method stub
		int users = userMapper.insert(record);
		return users;
	}

	@Override
	public User selectByUserId(Integer userId) {
		// TODO Auto-generated method stub
		User users = userMapper.selectByUserId(userId);
		return users;
	}

	@Override
	public int updateByUserId(User record) {
		// TODO Auto-generated method stub
		int users= userMapper.updateByUserId(record);
		return users;
	}

	@Override
	public List<User> selectAll(Integer page,Integer limit) {
		// TODO Auto-generated method stub
		PageHelper.startPage(page,limit);
		List<User> users=userMapper.selectAll();
		return users;
	}


	/**
	 * 多条件查询用户  -----by guozongchao
	 *
	 * @param user
	 * @param page
	 * @param limit
	 * @return
	 */
	@Override
	public List<User> searchUsers(User user, Integer page, Integer limit) {
		PageHelper.startPage(page, limit);
		List<User> users = userMapper.searchUsers(user);
		return users;
	}
}
