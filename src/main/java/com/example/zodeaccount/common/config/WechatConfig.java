package com.example.zodeaccount.common.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Getter
public class WechatConfig {

    @Value("${wechat.account.appId}")
    private String appId;

    @Value("${wechat.account.secret}")
    private String appSecrect;

    @Value("${wechat.account.token}")
    private String token;

    @Value("${wechat.miniApp.account.appId}")
    private String miniAppId;

    @Value("${wechat.miniApp.account.secret}")
    private String miniSecrect;

    @Value("${wechat.miniApp.account.msgDataFormat}")
    private String miniMsgDataFormat;

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(this.getMiniAppId());
        config.setSecret(this.getMiniSecrect());
        config.setMsgDataFormat(this.getMiniMsgDataFormat());

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
