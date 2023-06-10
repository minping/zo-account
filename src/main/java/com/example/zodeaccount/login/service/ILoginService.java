package com.example.zodeaccount.login.service;

import com.example.zodeaccount.common.pojo.Result;

public interface ILoginService {

    /**
     * 自动登录
     * @param openId
     * @param sessionId
     * @return
     */
    Result authLogin(String openId, String sessionId);

}
