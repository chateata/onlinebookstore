package com.shop.bookshop.controller;

import com.shop.bookshop.dao.AdminMapper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.pojo.Admin;
import com.shop.bookshop.util.ResultCode;
import com.shop.bookshop.util.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 获取所有管理员
     * @return
     */
    @GetMapping("/list")
    public ResultVO<List<Admin>> listAdmins() {
        List<Admin> admins = adminMapper.selectAll();
        return new ResultVO<>(ResultCode.SUCCESS, admins.size(), admins);
    }

    /**
     * 添加管理员
     * @param admin
     * @return
     */
    @PostMapping("/insert")
    public ResultVO<Void> insertAdmin(@RequestBody @Valid Admin admin) {
        Admin existing = adminMapper.selectByAdminName(admin.getAdminName());
        if (existing != null) {
            throw new CustomizeException(ResultCode.FAILED, "管理员用户名已存在");
        }
        adminMapper.insert(admin);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    /**
     * 更新管理员密码
     * @param admin
     * @return
     */
    @PutMapping("/update")
    public ResultVO<Void> updateAdmin(@RequestBody Admin admin) {
        Admin existing = adminMapper.selectByAdminName(admin.getAdminName());
        if (existing == null) {
            throw new CustomizeException(ResultCode.FAILED, "管理员不存在");
        }
        adminMapper.updateByAdminName(admin);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }

    /**
     * 删除管理员
     * @param adminName
     * @return
     */
    @DeleteMapping("/{adminName}")
    public ResultVO<Void> deleteAdmin(@PathVariable String adminName) {
        Admin existing = adminMapper.selectByAdminName(adminName);
        if (existing == null) {
            throw new CustomizeException(ResultCode.FAILED, "管理员不存在");
        }
        adminMapper.deleteByAdminName(adminName);
        return new ResultVO<>(ResultCode.SUCCESS, null);
    }
}
