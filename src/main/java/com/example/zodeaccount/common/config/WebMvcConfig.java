package com.example.zodeaccount.common.config;

import com.example.zodeaccount.handler.LoginHandler;
import com.example.zodeaccount.handler.PrintMethodHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加拦截 注入拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginHandler loginHandler;
    @Autowired
    PrintMethodHandler printMethodHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginHandler);
        registry.addInterceptor(printMethodHandler);

    }
}
