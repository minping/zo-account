package com.example.zodeaccount.wechart.pojo;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 响应消息体
 */
@Data
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseMsgBody {

    /**接收方帐号（收到的OpenID）*/
    private String ToUserName;
    /** 开发者微信号 */
    private String FromUserName;
    /** 消息创建时间 */
    private long CreateTime;
    /** 消息类型*/
    private String MsgType;
    /** 文本消息的消息体 */
    private String Content;

}
