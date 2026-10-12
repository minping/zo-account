package com.example.zodeaccount.handler;

import com.alibaba.fastjson.JSON;
import com.example.zodeaccount.common.util.WebUtils;
import com.example.zodeaccount.redis.RedisKey;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.common.pojo.UserDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器 验证token
 */
@Component
public class LoginHandler implements HandlerInterceptor {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (handlerMethod.hasMethodAnnotation(AuthIgnore.class)){
            return true;
        }
        if (handlerMethod.hasMethodAnnotation(PlugInAuth.class)){
            String code = request.getHeader("code");
            if ("DreamSoft!@#@123".equals(code)){
                return true;
            }else{
                WebUtils.renderString(response,JSON.toJSONString(Result.me().error().setMessage("code失效或过期")));
                return false;
            }
        }
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)){
            WebUtils.renderString(response,JSON.toJSONString(Result.me().error().setMessage("登录信息失效或过期")));
            return false;
        }
        token = token.replace("Bearer: ","");
        String userJson = redisTemplate.opsForValue().get(RedisKey.TOKEN + token);
        if (StringUtils.isBlank(userJson)){
            WebUtils.renderString(response,JSON.toJSONString(Result.me().error().setMessage("登录信息失效或过期")));
            return false;
        }
        UserDetail userDetail = JSON.parseObject(userJson, UserDetail.class);
        UserTheadLocal.put(userDetail);
        return true;
    }

    private static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserTheadLocal.remove();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
