package com.picchealth.inside.utils;

import com.picchealth.inside.constant.CommonEnum;
import org.apache.commons.lang.StringUtils;

/**
 * redis存放登录信息
 * @author by zhangbr
 * @date 2020/5/8.
 */
public class RedisLoginInfo {

    private String pcToken;

    private String mobToken;

    private String padToken;

    private String phone;

    public String getPcToken() {
        return pcToken;
    }

    public void setPcToken(String pcToken) {
        this.pcToken = pcToken;
    }

    public String getMobToken() {
        return mobToken;
    }

    public void setMobToken(String mobToken) {
        this.mobToken = mobToken;
    }

    public String getPadToken() {
        return padToken;
    }

    public void setPadToken(String padToken) {
        this.padToken = padToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 根据登录方式获取token
     * @param loginMode 登录方式
     * @return String
     */
    public String getServerToken(String loginMode){
        if (CommonEnum.USER_LOGIN_PC.getCode().equals(loginMode)){
            return pcToken;
        }else if (CommonEnum.USER_LOGIN_MOB.getCode().equals(loginMode)){
            return mobToken;
        }else if (CommonEnum.USER_LOGIN_PAD.getCode().equals(loginMode)){
            return padToken;
        }
        return null;
    }

    /**
     * 添加token
     * @param loginMode 登录方式
     * @param token token
     */
    public void setToken(String loginMode, String token, String phone){
        if (CommonEnum.USER_LOGIN_PC.getCode().equals(loginMode)){
            setPcToken(token);
        }else if (CommonEnum.USER_LOGIN_MOB.getCode().equals(loginMode)){
            setMobToken(token);
        }else if (CommonEnum.USER_LOGIN_PAD.getCode().equals(loginMode)){
            setPadToken(token);
        }
        setPhone(phone);
    }

    /**
     * 验证token是否一致
     * @param token token
     * @return boolean
     */
    public boolean checkToken(String token) {
        return !StringUtils.isBlank(token) && (token.equals(pcToken) || token.equals(mobToken) || token.equals(padToken));
    }
}
