package com.hzvtc1063.filemanage.enums;

import lombok.Getter;

/**
 * @author hangzhi1063
 * @date 2020/12/9 19:19
 */
@Getter
public enum  UserEnum {

    USERNAME_EXIST("用户名已存在"),
    PASSWORD_NOTSAME("密码错误"),
    USER_NOTEXIST("用户不存在"),
    USER_NOTLOGIN("用户未登录"),
    USER_LOCKED("您的账户已被锁定,请联系管理员"),
    USER_PERMISSION_NOTENOUGH("您的权限不足");



    private String msg;

    UserEnum(String msg){
       this.msg =msg;
    }
}
