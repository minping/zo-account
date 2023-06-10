package com.example.zodeaccount.wechart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.zodeaccount.common.util.UUidUtil;
import com.example.zodeaccount.common.config.WechatConfig;
import com.example.zodeaccount.common.mapper.AccountMapper;
import com.example.zodeaccount.common.mapper.UserMapper;
import com.example.zodeaccount.common.pojo.Account;
import com.example.zodeaccount.wechart.pojo.Bill;
import com.example.zodeaccount.wechart.pojo.ReceiveMsgBody;
import com.example.zodeaccount.wechart.pojo.ResponseMsgBody;
import com.example.zodeaccount.common.pojo.User;
import com.example.zodeaccount.wechart.service.IWechatDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IWechatDetailServiceImpl implements IWechatDetailService {

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    WechatConfig wechatConfig;

    @Autowired
    UserMapper userMapper;
    @Autowired
    AccountMapper accountMapper;
    /**
     * 记账具体业务处理
     *
     * @param receiveMsgBody
     * @return
     */
    @Override
    public Object accounting(ReceiveMsgBody receiveMsgBody) {
        String openIdFromUser = receiveMsgBody.getFromUserName();
        logger.info("openId:"+openIdFromUser);
        // todo 验证身份
        User user = getUserByOpenId(openIdFromUser);
        // todo 存储记账数据
        saveAccount(receiveMsgBody,user);
        // todo 查找记账数据
        String content = getAccount(user);
        // todo 返回消息体
        ResponseMsgBody responseMsgBody = packageResponseMsg(receiveMsgBody, content);
        return responseMsgBody;
    }

    private User getUserByOpenId(String openId){
        Map<String, Object> userMap = userMapper.selectUserByOpenId(openId);
        User user = new User();
        if (Objects.isNull(userMap)){
            user.setSys_user_id(UUidUtil.getUUId());
            user.setOpenId(openId);
            int insert = userMapper.insert(user);
            if (insert>0){
                logger.info("新建用户成功:"+openId);
            }
        }else{
            user.setSys_user_id(userMap.get("sys_user_id").toString());
            user.setOpenId(openId);
        }
        logger.info("返回用户:"+user.getSys_user_id());
        return user;
    }

    private void saveAccount(ReceiveMsgBody receiveMsgBody,User user){
        String content = receiveMsgBody.getContent();
        String msgId = String.valueOf(receiveMsgBody.getMsgId());
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getMsg_id,msgId);
        Account data = accountMapper.selectOne(queryWrapper);
        if (!Objects.isNull(data)){
            logger.info("该消息已经被处理");
            return;
        }
        String money = "";
        String description;

        String regex1 = "\\d+\\.+\\d+";
        String regex2 = "\\d+";
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher1 = pattern1.matcher(content);
        Matcher matcher2 = pattern2.matcher(content);

        if (matcher2.find()){
            money = matcher2.group();
        }
        if (matcher1.find()){
            // 先匹配带小数的
            money = matcher1.group();
        }
        description = content.replaceAll(money,"").trim();
        BigDecimal moneyBigDecimal = new BigDecimal(money);
        long longValue = moneyBigDecimal.multiply(new BigDecimal(100)).longValue();
        Account account = new Account();
        account.setSys_account_id(UUidUtil.getUUId());
        account.setSys_user_id(user.getSys_user_id());
        account.setCreate_time(new Date());
        account.setMoney(longValue);
        account.setDescription(description);
        int insert = accountMapper.insert(account);
        if (insert > 0 ){
            logger.info("用户:"+user.getOpenId()+"成功添加一笔记账");
        }
    }

    private String getAccount(User user){
        Map<String,Object> monthCount = accountMapper.selectTheMonthMoneyByUserId(user.getSys_user_id());
        if (Objects.isNull(monthCount)){
            return "未找到您的记账记录";
        }
        String monthMoney = monthCount.get("total_money").toString();
        String monthNum = monthCount.get("num").toString();
        BigDecimal bigDecimal = new BigDecimal(monthMoney);
        double monthMoneyDouble = bigDecimal.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String content = "添加成功(●'◡'●)\n"
                + "本月已记录"+monthNum+"笔记账\n"
                + "共花费"+monthMoneyDouble+"元";
        return content;
    }

    private ResponseMsgBody packageResponseMsg(ReceiveMsgBody receiveMsgBody,String content){

        // 响应的消息
        ResponseMsgBody textMsg = new ResponseMsgBody();
        textMsg.setToUserName(receiveMsgBody.getFromUserName());
        textMsg.setFromUserName(receiveMsgBody.getToUserName());
        textMsg.setCreateTime(System.currentTimeMillis());
        textMsg.setMsgType(receiveMsgBody.getMsgType());
        textMsg.setContent(content);
        return textMsg;
    }

    /**
     * @param receiveMsgBody
     * @return
     */
    @Override
    public Object viewBill(ReceiveMsgBody receiveMsgBody) {
        String openIdFromUser = receiveMsgBody.getFromUserName();
        // todo 校验用户
        User user = getUserByOpenId(openIdFromUser);
        // todo 查看记账
        String content = receiveMsgBody.getContent();
        String userId = user.getSys_user_id();
        String responeContent = "";
        if ("1".equals(content)){
            //查看今日记账
            Map<String,Object> count = accountMapper.selectTheDayMoneyByUserId(userId);
            if (Objects.isNull(count)){
                responeContent = "暂未查到今日记账";
            }else{
                String totalMoney = count.get("total_money").toString();
                String num = count.get("num").toString();
                BigDecimal bigDecimal = new BigDecimal(totalMoney);
                double moneyDouble = bigDecimal.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                responeContent = "本周已记录"+num+"笔记账\n"
                        + "共花费"+moneyDouble+"元";
            }
        }else if("2".equals(content)){
            //查看今日记账明细
            List<Bill> bills = accountMapper.selectTheDayMoneyDetailByUserId(userId);
            if (CollectionUtils.isEmpty(bills)){
                responeContent = "暂未查到今日记账明细";
            }else{
                responeContent = parseBillText(bills);
            }

        }else if("3".equals(content)){
            //查看本周记账
            Map<String,Object> count = accountMapper.selectTheWeekMoneyByUserId(userId);
            if (Objects.isNull(count)){
                responeContent = "暂未查到本周记账";
            }else{
                String totalMoney = count.get("total_money").toString();
                String num = count.get("num").toString();
                BigDecimal bigDecimal = new BigDecimal(totalMoney);
                double moneyDouble = bigDecimal.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                responeContent = "本周已记录"+num+"笔记账\n"
                        + "共花费"+moneyDouble+"元";
            }
        }else if("4".equals(content)){
            //查看本周记账明细
            List<Bill> bills = accountMapper.selectTheWeekMoneyDetailByUserId(userId);
            if (CollectionUtils.isEmpty(bills)){
                responeContent = "暂未查到本周记账明细";
            }else{
                responeContent = parseBillText(bills);
            }
        }else if("5".equals(content)){
            //查看本月记账
            Map<String,Object> count = accountMapper.selectTheMonthMoneyByUserId(userId);
            if (Objects.isNull(count)){
                responeContent = "暂未查到本月记账";
            }else{
                String totalMoney = count.get("total_money").toString();
                String num = count.get("num").toString();
                BigDecimal bigDecimal = new BigDecimal(totalMoney);
                double moneyDouble = bigDecimal.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                responeContent =  "本月已记录"+num+"笔记账\n"
                        + "共花费"+moneyDouble+"元";
            }
        }else if("6".equals(content)){
            //查看本月记账明细
            List<Bill> bills = accountMapper.selectTheMonthMoneyDetailByUserId(userId);
            if (CollectionUtils.isEmpty(bills)){
                responeContent = "暂未查到本月记账明细";
            }else{
                responeContent = parseBillText(bills);
            }
        }

        // todo 处理响应数据
        ResponseMsgBody responseMsgBody = packageResponseMsg(receiveMsgBody, responeContent);
        return responseMsgBody;
    }

    private String parseBillText(List<Bill> bills){
        String content = "查询成功!\n";
        BigDecimal sumMoney = bills.stream().map(item -> new BigDecimal(item.getMoney())).reduce(new BigDecimal(0), (x, y) -> x.add(y));
        String sumMoneyStr = sumMoney.toString();
        for( Bill bill : bills){
            content += bill.getDate()+" "+bill.getDescription()+bill.getMoney()+"\n";
        };
        content += "共计"+sumMoneyStr+"元";
        return content;
    }

}
