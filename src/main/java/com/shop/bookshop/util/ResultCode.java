package com.shop.bookshop.util;

public enum ResultCode {
    SUCCESS(0,"操作成功"),
    FAILED(1000,"操作失败"),
    USER_NOT_LOGGED_IN(1001,"请先登录"),
    USER_NOT_FOUND(1002,"用户不存在"),
    PASSWORD_ERROR(1003,"密码错误"),
    RECORD_NOT_FOUND(1004,"暂时查询不到记录"),
    ARGUMENT_NOT_VALID(1005,"参数校验错误"),
    ACCESS_DENIED(1006,"没有权限访问"),
    BOOK_NOT_FOUND(2005,"书籍不存在"),
    ORDER_NOT_FOUND(2004,"订单不存在"),
    ORDER_ITEM_NOT_FOUND(2003,"订单项不存在"),
    INSUFFICIENT_STOCK(2001,"库存不足"),
    INSUFFICIENT_CREDIT(2002,"余额或信用不足"),
    PURCHASE_ORDER_ITEM_NOT_FOUND(2006,"采购单项不存在"),
    SHIPPING_NOT_ALLOWED(3001,"无法发货：余额或信用不足"),
    SHORTAGE_CREATED(3002,"已创建缺书记录"),
    PURCHASE_ORDER_CREATED(3003,"采购单已创建"),
    UNKNOWN_ERROR(-1,"出错,请检查输入是否合法");
    private int code;
    private String msg;

    ResultCode(int code, String msg){
        this.code=code;
        this.msg=msg;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
