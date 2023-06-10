package com.example.zodeaccount.wechart.controller;

import com.example.zodeaccount.handler.AuthIgnore;
import com.example.zodeaccount.wechart.pojo.ReceiveMsgBody;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.wechart.service.IWechatService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class WechatController {

    @Autowired
    IWechatService iWechatService;

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @AuthIgnore
    @RequestMapping(value = "/wechat",method = RequestMethod.GET)
    public String checkSignature(HttpServletRequest request, HttpServletResponse response){
        return iWechatService.checkSignature(request,response);
    }

    /**
     * 接收用户消息
     * @param receiveMsgBody 消息
     * @return
     */
    @AuthIgnore
    @RequestMapping(value = "/wechat",method = RequestMethod.POST,produces = {"application/xml; charset=UTF-8"})
    public Object wechat(HttpServletRequest request, HttpServletResponse response,@RequestBody ReceiveMsgBody receiveMsgBody) throws IOException {
        Object responseBody = null;
        logger.info("接收到的消息：{}", receiveMsgBody);
        // 如果包含，则是触发事件
        String msgType = receiveMsgBody.getMsgType();
        System.out.println("msgType = " + msgType);
        if ("event".equals(msgType)){
            // 关注事件处理
            responseBody = iWechatService.replyFollow(receiveMsgBody);
        }else{
            // 非事件 普通消息回复
            responseBody = iWechatService.replyMessage(receiveMsgBody);
        }
        return responseBody;
    }

    @AuthIgnore
    @RequestMapping(value = "/wechat/getToken", method = RequestMethod.GET)
    public Result getToken(){
        return Result.me().success().setData(iWechatService.getToken());
    }

    @AuthIgnore
    @RequestMapping(value = "/wechat/code2session",method = RequestMethod.GET)
    public Result code2session(String code){
        logger.info("code==>"+code);
        return iWechatService.code2session(code);
    }

    @AuthIgnore
    @RequestMapping(value = "/wechat/code2session2",method = RequestMethod.GET)
    public Result code2session2(String code){
        logger.info("code==>"+code);
        return iWechatService.code2session2(code);
    }
}
