package com.picchealth.inside.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.picchealth.inside.utils.MD5Utils;
import com.picchealth.inside.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 重复请求进行拦截处理
 * 并把当前请求进行重复标识 ，
 * 请求完成后进行移除 (详见 RepeatRequestAfterFilter )
 * @author liuzm
 */
@Component
@Slf4j
public class RepeatRequestFilter extends ZuulFilter {

    public static final String REPEAT_FLAG = "REPEAT_FLAG";


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
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRemoteAddr() + request.getServletPath();
        try {
            String params = null;
            //表单中获取参数
            if (null == request){
                return null;
            }
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (!parameterMap.isEmpty()) {
                params = JSON.toJSONString(parameterMap);
            }
            //body中获取参数
            if (!ctx.isChunkedRequestBody()) {
                ServletInputStream inp = ctx.getRequest().getInputStream();
                if (inp != null) {
                    if (StringUtils.isEmpty(params)) {
                        params = IOUtils.toString(inp, "utf-8");
                    } else {
                        params += IOUtils.toString(inp, "utf-8");
                    }
                }
            }
            //获取用户信息token
            String userToken = request.getHeader("token");
            //  验证参数是否被修改  end
            log.info("token:{}  url：{} params {}", userToken, url, request.getMethod());
            //为空添加入Redis，不为空   返回错误信息重复提交
            String md5Key = MD5Utils.encodeMD5(userToken + url + params);
            log.info("md5Key==================>"+md5Key);
            request.setAttribute(REPEAT_FLAG, md5Key);
            if (null != userToken){
                log.info("redis=======md5Key==================>"+RedisUtils.get(md5Key));
                if (null == RedisUtils.get(md5Key)) {
                    RedisUtils.set(md5Key, md5Key, 1);//默认1秒后可以再次请求同一接口
                } else {
                    /*ctx.getResponse().setCharacterEncoding("utf-8");
                    ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
                    ctx.setResponseStatusCode(200);// 返回错误码
                    ctx.setResponseBody("您的请求太频繁，请稍后尝试！");// 返回错误内容
                    ctx.set("isSuccess", false);*/
                    log.info("请求过于频繁");
                    log.info("客户地址:{}  请求地址：{} 请求方式 {}", request.getRemoteHost(), request.getRequestURL().toString(), request.getMethod());
                    log.info("params:{} ", params);
                    return null;
                }
            }
        } catch (Exception e) {
            log.warn("{}重复请求校验出错：{}", request.getRequestURL(), e.getMessage());
        }

        return null;
    }

}