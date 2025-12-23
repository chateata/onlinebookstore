package com.shop.bookshop.controller;

import com.github.pagehelper.PageInfo;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.service.UserService;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {
   @Autowired
   private UserService userService;
    @Resource
    private com.shop.bookshop.dao.CreditLevelMapper creditLevelMapper;
    @Resource
    private com.shop.bookshop.dao.UserMapper userMapper;
    /**
     * 分页获取用户列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public ResultVO<List<User>> getUserList(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {
        List<User> users=  userService.selectAll(page,limit);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new ResultVO<List<User>>(ResultCode.SUCCESS,(int)pageInfo.getTotal(),users);
    }

    /**
     * 删除指定用户ID
     * @param userId
     * @return
     */
    @DeleteMapping("/list/{userId}")
    public ResultVO<Void> deleteUser(@PathVariable("userId") Integer userId) {
        int users =userService.deleteByUserId(userId);
        return new ResultVO<Void>(ResultCode.SUCCESS,null);
    }

/*    暂时不需要
    @PostMapping("/insert")
    public ResultVO insertUser(User record) {
        int users =userService.insert(record);
        return new ResultVO(ResultCode.SUCCESS,null);
    }
    */

   /* @GetMapping("/search")
    public ResultVO searchUserById(Integer userId) {
        User users =userService.selectByUserId(userId);
        if(users!=null)
        return new ResultVO(ResultCode.SUCCESS,users);
        else
        return new ResultVO(ResultCode.USER_NOT_FOUND,null);
    }*/

   /*  原来
   @PostMapping("/update1")
    public ResultVO updateUser( User record) {
        int users =userService.updateByUserId(record);
        return new ResultVO(ResultCode.SUCCESS,null);
    }*/

    /**
     * 修改    添加验证注解和RequestBody注解  
     * @param record
     * @return
     */
    @PostMapping("/update")
    public ResultVO<Void> updateUser(@RequestBody @Valid User record) {
        int users =userService.updateByUserId(record);
        return new ResultVO<Void>(ResultCode.SUCCESS,null);
    }

    @GetMapping("/search")
    public ResultVO<List<User>> searchUsers(User user, Integer page, Integer limit) {
        if (user.getUserName().isEmpty()) {
            user.setUserName(null);
        }
        if (user.getEmail().isEmpty()) {
            user.setEmail(null);
        }
        List<User> users = userService.searchUsers(user, page, limit);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new ResultVO<List<User>>(ResultCode.SUCCESS, (int) pageInfo.getTotal(), users);
    }

    /**
     * 获取当前登录用户的余额与信用等级信息
     */
    @GetMapping("/credit")
    public ResultVO<Map<String,Object>> getUserCredit(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj == null) {
            return new ResultVO(ResultCode.USER_NOT_LOGGED_IN);
        }
        User sessionUser = (User) obj;
        User fresh = userService.selectByUserId(sessionUser.getUserId());
        if (fresh == null) {
            return new ResultVO(ResultCode.USER_NOT_FOUND);
        }
        com.shop.bookshop.pojo.CreditLevel cl = null;
        if (fresh.getCreditLevelId() != null) {
            cl = creditLevelMapper.selectByLevelId(fresh.getCreditLevelId());
        }
        Map<String,Object> data = new HashMap<>();
        data.put("accountBalance", fresh.getAccountBalance());
        data.put("creditLevel", cl);
        return new ResultVO<Map<String,Object>>(ResultCode.SUCCESS, data);
    }

}
