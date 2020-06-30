package com.picchealth.inside.constant;

/**
 * @author by zhangbr
 * @date 2020/3/24.
 */
public enum CommonEnum {
    /**
     * 登录方式
     */
    USER_LOGIN_PC("PC", "电脑网页"),
    USER_LOGIN_MOB("MOB", "手机设备"),
    USER_LOGIN_PAD("PAD", "pad设备"),
    /**
     * 用户登录
     */
    USER_LOGIN("userLoginHashMap", "用户登录"),
    ;


    CommonEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
