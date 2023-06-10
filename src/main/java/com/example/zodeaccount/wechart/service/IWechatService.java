package com.example.zodeaccount.wechart.service;

import com.example.zodeaccount.wechart.pojo.ReceiveMsgBody;
import com.example.zodeaccount.common.pojo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IWechatService {

    /**
     * 获取token
     * @return
     */
    String getToken();

    /**
     * 第一次维护公众号基础设置时需要的验证token方法
     * @param request
     * @param response
     * @return
     */
    String checkSignature(HttpServletRequest request, HttpServletResponse response);

    /**
     * 关注事件回复处理
     * @param receiveMsgBody
     * @return
     */
    Object replyFollow(ReceiveMsgBody receiveMsgBody);

    /**
     * 普通消息回复处理
     * @param receiveMsgBody
     * @return
     */
    Object replyMessage(ReceiveMsgBody receiveMsgBody);

    Result test();

    /**
     * 微信小程序 code换session
     * @param code
     * @return
     */
    Result code2session(String code);

    Result code2session2(String code);
}
