package com.example.zodeaccount.wechart.service;

import com.example.zodeaccount.wechart.pojo.ReceiveMsgBody;

public interface IWechatDetailService {

    /**
     * 记账具体业务处理
     * @return
     */
    Object accounting(ReceiveMsgBody receiveMsgBody);

    /**
     *
     * @param receiveMsgBody
     * @return
     */
    Object viewBill(ReceiveMsgBody receiveMsgBody);
}
