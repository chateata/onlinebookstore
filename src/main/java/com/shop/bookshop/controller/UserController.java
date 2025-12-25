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
        userService.deleteByUserId(userId);
        return new ResultVO<Void>(ResultCode.SUCCESS,null);
    }

    /**
     * 管理员调整用户余额（delta 可正可负），返回最新余额
     */
    @PostMapping("/adjustBalance")
    public ResultVO<java.math.BigDecimal> adjustBalance(@RequestParam Integer userId, @RequestParam java.math.BigDecimal delta) {
        if (userId == null || delta == null) {
            return ResultVO.error(ResultCode.FAILED, "参数错误");
        }
        com.shop.bookshop.pojo.User u = userService.selectByUserId(userId);
        if (u == null) {
            return new ResultVO<>(ResultCode.RECORD_NOT_FOUND, null);
        }
        java.math.BigDecimal cur = u.getAccountBalance() == null ? java.math.BigDecimal.ZERO : u.getAccountBalance();
        java.math.BigDecimal next = cur.add(delta);
        if (next.compareTo(java.math.BigDecimal.ZERO) < 0) {
            return ResultVO.error(ResultCode.FAILED, "余额不能为负");
        }
        u.setAccountBalance(next);
        userService.updateByUserId(u);
        return new ResultVO<>(ResultCode.SUCCESS, next);
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
        userService.updateByUserId(record);
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
            return ResultVO.<Map<String,Object>>error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User sessionUser = (User) obj;
        User fresh = userService.selectByUserId(sessionUser.getUserId());
        if (fresh == null) {
            return ResultVO.<Map<String,Object>>error(ResultCode.USER_NOT_FOUND);
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

    /**
     * 获取当前登录用户的基本信息（用户名、密码等）
     */
    @GetMapping("/info")
    public ResultVO<Map<String,Object>> getUserInfo(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj == null) {
            return ResultVO.<Map<String,Object>>error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User sessionUser = (User) obj;
        User fresh = userService.selectByUserId(sessionUser.getUserId());
        if (fresh == null) {
            return ResultVO.<Map<String,Object>>error(ResultCode.USER_NOT_FOUND);
        }
        Map<String,Object> data = new HashMap<>();
        data.put("userId", fresh.getUserId());
        data.put("userName", fresh.getUserName());
        data.put("email", fresh.getEmail());
        data.put("joinTime", fresh.getJoinTime());
        return new ResultVO<Map<String,Object>>(ResultCode.SUCCESS, data);
    }

    /**
     * 修改当前登录用户的密码
     */
    @PostMapping("/changePassword")
    public ResultVO<Void> changePassword(@RequestBody Map<String, String> passwordData, HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj == null) {
            return ResultVO.<Void>error(ResultCode.USER_NOT_LOGGED_IN);
        }

        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        if (oldPassword == null || newPassword == null || newPassword.trim().isEmpty()) {
            return ResultVO.<Void>error(ResultCode.FAILED, "密码不能为空");
        }

        if (newPassword.length() < 6) {
            return ResultVO.<Void>error(ResultCode.FAILED, "新密码至少6个字符");
        }

        User sessionUser = (User) obj;
        User fresh = userService.selectByUserId(sessionUser.getUserId());
        if (fresh == null) {
            return ResultVO.<Void>error(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!oldPassword.equals(fresh.getPassword())) {
            return ResultVO.<Void>error(ResultCode.FAILED, "旧密码错误");
        }

        // 更新密码
        fresh.setPassword(newPassword);
        userService.updateByUserId(fresh);

        return new ResultVO<Void>(ResultCode.SUCCESS, null);
    }

}
