package com.example.zodeaccount.wechart.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSONObject;
import com.example.zodeaccount.common.config.WechatConfig;
import com.example.zodeaccount.wechart.pojo.ReceiveMsgBody;
import com.example.zodeaccount.wechart.pojo.ResponseMsgBody;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.common.pojo.User;
import com.example.zodeaccount.common.util.HttpClientUtil;
import com.example.zodeaccount.wechart.service.IWechatDetailService;
import com.example.zodeaccount.wechart.service.IWechatService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IWechatServiceImpl implements IWechatService {

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    WechatConfig wechatConfig;
    @Autowired
    IWechatDetailService iWechatDetailService;
    @Autowired
    WxMaService wxMaService;

    @Override
    public String getToken() {
        String accessToken = "";
        try{
            accessToken = wxMaService.getAccessToken();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        logger.info("获取到token:"+accessToken);
        return accessToken;
    }

    /**
     * 关注事件回复处理
     * @param receiveMsgBody
     * @return
     */
    @Override
    public Object replyFollow(ReceiveMsgBody receiveMsgBody) {
        logger.info("发送关注自动回复");
        // 响应的消息
        String content = "欢迎您的关注\n"+
                "如果对我有一些想法或建议\n" +
                "欢迎输入内容(●'◡'●)";
        //String content = "欢迎您的关注\n" +
        //        "本号提供以下功能以及指令\n" +
        //        "zo记账,快速记下每一笔支出~\n" +
        //        "添加一笔记账:输入[描述+金额]\n" +
        //        "例如: [吃黄焖鸡花了25] \n" +
        //        "  或 [30.8用在京东购物]\n" +
        //        "  或 [买了20.55元水果]\n" +
        //        "  (只要带有数字的金额即可)\n" +
        //        "输入[1]:查看今日记账\n" +
        //        "输入[2]:查看今日记账明细\n" +
        //        "输入[3]:查看本周记账\n" +
        //        "输入[4]:查看本周记账明细\n" +
        //        "输入[5]:查看本月记账\n" +
        //        "输入[6]:查看本月记账明细\n"+
        //        "输入[帮助]即可查看所有指令\n" +
        //        "  \n" +
        //        "如果对我有一些想法或建议\n" +
        //        "欢迎输入内容(●'◡'●)";
        ResponseMsgBody textMsg = new ResponseMsgBody();
        textMsg.setToUserName(receiveMsgBody.getFromUserName());
        textMsg.setFromUserName(receiveMsgBody.getToUserName());
        textMsg.setCreateTime(System.currentTimeMillis());
        textMsg.setMsgType("text");
        textMsg.setContent(content);
        return textMsg;
    }

    @Override
    public Object replyMessage( ReceiveMsgBody receiveMsgBody) {
        String content = receiveMsgBody.getContent();
        String msgType = receiveMsgBody.getMsgType();
        if ("text".equals(msgType)){
            if ("帮助".equals(content)){
                return replyFollow(receiveMsgBody);
            }
            if ("1".equals(content)
                || "2".equals(content)
                || "3".equals(content)
                || "4".equals(content)
                || "5".equals(content)
                || "6".equals(content)
            ){
                // 查看账单业务处理
                return iWechatDetailService.viewBill(receiveMsgBody);
            }
            // 文本消息回复
            if (valiateCount(content)){
                // 判断是否包含数字(12.00 12) 如包含则用于记账业务
                return iWechatDetailService.accounting(receiveMsgBody);
            }
        }

        // 响应的消息
        ResponseMsgBody textMsg = new ResponseMsgBody();
        textMsg.setToUserName(receiveMsgBody.getFromUserName());
        textMsg.setFromUserName(receiveMsgBody.getToUserName());
        textMsg.setCreateTime(System.currentTimeMillis());
        textMsg.setMsgType(receiveMsgBody.getMsgType());
        textMsg.setContent("后台已收到消息,感谢您的建议(●'◡'●)");
        return textMsg;
    }

    /**
     * 判断是否包含数字(12.00 12) 如包含则用于记账功能
     * @param txt
     * @return
     */
   public boolean valiateCount(String txt){
        boolean flag = false;
        if (StringUtils.isBlank(txt)){
            return flag;
        }
       String regex1 = "\\d+\\.+\\d+";
       String regex2 = "\\d+";

       Pattern pattern1 = Pattern.compile(regex1);
       Pattern pattern2 = Pattern.compile(regex2);
       Matcher matcher1 = pattern1.matcher(txt);
       Matcher matcher2 = pattern2.matcher(txt);

       if (matcher1.find() || matcher2.find()){
           flag = true;
       }
        return flag;
   }






    @Override
    public Result test() {
        User user = new User();
        user.setSys_user_id("426453c8e6aa4b7d90ba1a88d995bc08");
        String account = "";
        return Result.me().success().setData(account);
    }


    @Override
    public String checkSignature(HttpServletRequest request, HttpServletResponse response){
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        logger.info("signature = {}", signature);
        logger.info("timestamp = {}", timestamp);
        logger.info("nonce = {}", nonce);
        logger.info("echostr = {}", echostr);
        // 第一步：自然排序
        String[] tmp = {wechatConfig.getToken(), timestamp, nonce};
        Arrays.sort(tmp);
        // 第二步：sha1 加密
        String sourceStr = StringUtils.join(tmp);
        System.out.println("sourceStr = " + sourceStr);
        String localSignature = DigestUtils.sha1Hex(sourceStr);
        logger.info("localSignature = {}", localSignature);
        // 第三步：验证签名
        if (signature.equals(localSignature)) {
            return echostr;
        }
        return echostr;
    }

    /**
     * 微信小程序 code换session
     *
     * @param code
     * @return
     */
    @Override
    public Result code2session(String code) {
        WxMaJscode2SessionResult wx = null;

        try {
            wx = wxMaService.jsCode2SessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return Result.me().success().setData(wx);
    }

    @Override
    public Result code2session2(String code) {
        String JSCODE_TO_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> params = new HashMap(8);
        params.put("appid", wechatConfig.getMiniAppId());
        params.put("secret", wechatConfig.getAppSecrect());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        String apiUrl = JSCODE_TO_SESSION_URL.concat("?appid=").concat(wechatConfig.getMiniAppId())
                .concat("&secret=").concat(wechatConfig.getAppSecrect())
                .concat("&js_code=").concat(code)
                .concat("&grant_type=authorization_code");
        logger.info("apiUrl==>"+apiUrl);
        String result = HttpClientUtil.doGet(apiUrl, "");
        JSONObject jsonObject = JSONObject.parseObject(result);
        return Result.me().success().setData(jsonObject);
    }
}
