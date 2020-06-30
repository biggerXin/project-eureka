package com.picchealth.inside.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.picchealth.inside.constant.CommonEnum;
import com.picchealth.inside.utils.RedisLoginInfo;
import com.picchealth.inside.utils.RedisUtils;
import com.picchealth.inside.vo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author by zhangbr
 * @date 2020/5/9.
 */
@ConfigurationProperties("picchealth")
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    /**
     * 客户端设置一个月超时
     */
    private static final Long LOGIN_TIME_LIMIT = 2592000L;

    private String[] accessUrls;

    public String[] getAccessUrls() {
        return accessUrls;
    }

    public void setAccessUrls(String... accessUrls) {
        this.accessUrls = accessUrls;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run(){
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String url = request.getRequestURI();
        String token = request.getHeader("token");
        String userId = request.getHeader("userId");
        String mode = request.getHeader("mode");
        for (String accessUrl : accessUrls) {
            if (accessUrl.equals(url)) {
                log.debug("URL:{},== pass", url);
                return null;
            }
            //匹配过滤校验
            if (accessUrl.endsWith("**")) {
                String regex = accessUrl.replaceAll("\\*\\*", ".*");
                if (isMatcher(regex, url)) {
                    log.debug("URL:{},== pass", url);
                    return null;
                }
            }
        }
        String auth = "success";
        try {
            boolean isLogin = checkAuth(userId, token, mode);
            if (!isLogin){
                auth = "fail";
                ApiResponse result = new ApiResponse().auth();
                requestContext(requestContext, result);
            }
        } catch (Exception e) {
            auth = "fail";
            ApiResponse result = new ApiResponse().exception();
            requestContext(requestContext, result);
        }
        log.debug("URL:{},===auth:{}", url, auth);
        return null;
    }

    private void requestContext(RequestContext requestContext, ApiResponse result){
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(200);
        requestContext.addZuulRequestHeader("Content-Type", "application/json;charset=UTF-8");
        requestContext.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
        requestContext.setResponseBody(JSONObject.toJSONString(result));
    }

    /**
     * 匹配是否符合表达式
     * @param regex regex
     * @param content content
     * @return boolean
     */
    private static boolean isMatcher(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        if (!StringUtils.isEmpty(content)){
            boolean matches = content.matches("^/picchealth/.*");
            if (matches){
                Matcher matcher = pattern.matcher(content);
                return matcher.matches();
            }
        }
        return false;
    }
    /**
     * 验证token有效性
     * @param userId 用户ID
     * @param token token
     * @param mode mode
     * @return boolean
     */
    private static boolean checkAuth(String userId, String token, String mode) {
        String key = CommonEnum.USER_LOGIN.getCode();
        if (StringUtils.isEmpty(userId)) {
            log.debug("userId:{},===token:{}，===mode:{}", userId, token, mode);
            return false;
        }
        if (!CommonEnum.USER_LOGIN_PC.getCode().equals(mode) &&
                !CommonEnum.USER_LOGIN_MOB.getCode().equals(mode) &&
                !CommonEnum.USER_LOGIN_PAD.getCode().equals(mode)) {
            mode = CommonEnum.USER_LOGIN_MOB.getCode();
        }
        log.debug("userId:{},===token:{}，===mode:{}", userId, token, mode);
        if (CommonEnum.USER_LOGIN_PC.getCode().equals(mode)) {
            return true;
        }
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        RedisLoginInfo loginInfo = RedisUtils.hGetObject(key, userId, RedisLoginInfo.class);
        log.debug("loginInfo:{}", JSONObject.toJSONString(loginInfo));
        if (loginInfo == null){
            return false;
        }
        if (!loginInfo.checkToken(token)){
            return false;
        }
        //token续费
        RedisUtils.hSet(key, userId, JSONObject.toJSONString(loginInfo), LOGIN_TIME_LIMIT);
        return true;
    }
}
