package com.picchealth.inside.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.picchealth.inside.vo.ApiResponse;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author by zhangbr
 * @date 2020/5/9.
 */
@Component
public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        boolean isThrow = ctx.getThrowable() != null;
        boolean isRan = !ctx.getBoolean("sendErrorFilter.ran", false);
        Object zuul = ctx.getRequest().getAttribute("javax.servlet.error.exception");
        boolean isException = zuul instanceof ZuulException;
        return isThrow || isRan || isException;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        response.setStatus(200);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            ApiResponse result = new ApiResponse().exception();
            out.write(JSONObject.toJSONBytes(result));
            out.flush();
            requestContext.setResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
