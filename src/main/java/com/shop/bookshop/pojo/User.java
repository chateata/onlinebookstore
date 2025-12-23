package com.shop.bookshop.pojo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.math.BigDecimal;

public class User {
    private Integer userId;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "[0-9A-Za-z_]{3,15}",message = "用户名只能是3~15位字母、数字或者下划线")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6,message = "密码至少6个字符")
    private String password;

    @Email(message = "邮箱格式错误")
    private String email;

    private String avatar;

    private Date joinTime;
    private BigDecimal accountBalance;
    private Integer creditLevelId;
    private CreditLevel creditLevel;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Integer getCreditLevelId() {
        return creditLevelId;
    }

    public void setCreditLevelId(Integer creditLevelId) {
        this.creditLevelId = creditLevelId;
    }

    public CreditLevel getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(CreditLevel creditLevel) {
        this.creditLevel = creditLevel;
    }
}