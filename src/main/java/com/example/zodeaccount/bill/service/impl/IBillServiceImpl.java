package com.example.zodeaccount.bill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.zodeaccount.bill.pojo.Day2Bill;
import com.example.zodeaccount.bill.service.IBillService;
import com.example.zodeaccount.common.mapper.AccountMapper;
import com.example.zodeaccount.common.pojo.Account;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.common.pojo.UserDetail;
import com.example.zodeaccount.common.util.UUidUtil;
import com.example.zodeaccount.handler.UserTheadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IBillServiceImpl implements IBillService {

    @Autowired
    AccountMapper accountMapper;

    @Override
    public Result getMyBillData(String yearMonth) {
        UserDetail userDetail = UserTheadLocal.get();
        String userId = userDetail.getSys_user_id();
        String linkUserId = userDetail.getLinkUserId();
        Map<String,Object> para = new HashMap<>();
        para.put("userId",userId);
        para.put("linkUserId", (StringUtils.isNotBlank(linkUserId)?linkUserId:""));
        para.put("yearMonth", (StringUtils.isNotBlank(yearMonth)?yearMonth:""));
        List<Map<String, Object>> maps = accountMapper.selectMyBillByUserId(para);
        Map<String, List<Map<String, Object>>> days = maps.stream().collect(Collectors.groupingBy(item -> item.get("create_time").toString()));
        List<Day2Bill> resultList = new LinkedList<>();
        days.entrySet().forEach( day ->{
            Day2Bill day2Bill = new Day2Bill();
            day2Bill.setDay(day.getKey());
            day2Bill.setBills(day.getValue());
            List<Map<String, Object>> bills = day.getValue();
            BigDecimal sumMoney = bills.stream().map(item -> new BigDecimal(item.get("money").toString())).reduce(new BigDecimal(0), (x, y) -> x.add(y));
            day2Bill.setSumMoney(String.valueOf(sumMoney.setScale(2)));
            resultList.add(day2Bill);
        });
        List<Day2Bill> sortResultList = resultList.stream().sorted( (x,y) ->  y.getDay().compareTo(x.getDay())).collect(Collectors.toList());
        return Result.me().success(sortResultList);
    }

    /**
     * 首页获取记账;头部总金额
     *
     * @param yearMonth
     * @return
     */
    @Override
    public Result getMyBillMoney(String yearMonth) {
        UserDetail userDetail = UserTheadLocal.get();
        String userId = userDetail.getSys_user_id();
        String linkUserId = userDetail.getLinkUserId();
        Map<String,Object> para = new HashMap<>();
        para.put("userId",userId);
        para.put("linkUserId", (StringUtils.isNotBlank(linkUserId)?linkUserId:""));
        para.put("yearMonth", (StringUtils.isNotBlank(yearMonth)?yearMonth:""));
        Map<String, Object> map = accountMapper.selectSumMoneyOneMonth(para);
        return Result.me().success(map);
    }

    /**
     * 添加一笔支出
     *
     * @param para
     * @return
     */
    @Override
    public Result addBill(Map<String, String> para) {
        System.out.println("para = " + para);
        String editId = para.get("editId");
        String description = para.get("description");
        String payout = para.get("payout");
        BigDecimal multiply = new BigDecimal(payout).multiply(new BigDecimal(100));
        String date = para.get("date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd");
        Date payTime;
        try {
            payTime = sdf.parse(date);
        } catch (ParseException e) {
            payTime = new Date();
            e.printStackTrace();
        }
        String type = para.get("type");
        JSONObject typeJson = JSONObject.parseObject(type);
        String typeId = typeJson.getString("id");
        String typeText = typeJson.getString("type");
        UserDetail userDetail = UserTheadLocal.get();
        Account account = new Account();
        account.setMoney(multiply.longValue());
        account.setSys_user_id(userDetail.getSys_user_id());
        account.setDescription(description);
        account.setCategories(typeText);
        account.setTypeId(typeId);
        account.setCreate_time(payTime);
        int result;
        if (StringUtils.isBlank(editId)){
            account.setSys_account_id(UUidUtil.getUUId());
            result = accountMapper.insert(account);
        }else{
            account.setSys_account_id(editId);
            result = accountMapper.updateById(account);
        }
        if (result > 0){
            return Result.me().success().setMessage("成功");
        }else{
            return Result.me().error().setMessage("失败");
        }
    }

    /**
     * 删除一笔支出
     *
     * @param para
     * @return
     */
    @Override
    public Result delBill(Map<String, String> para) {
        String editId = para.get("editId");
        if (StringUtils.isBlank(editId)){
            return Result.me().error().setMessage("参数错误,未找到id");
        }
        Account account = new Account();
        account.setSys_account_id(editId);
        account.setDel_flag("1");
        int i = accountMapper.updateById(account);
        return Result.me().success().setData(i);
    }

    /**
     * 统计月支出
     *
     * @param yearMonth
     * @return
     */
    @Override
    public Result getStatics(String yearMonth) {
        UserDetail userDetail = UserTheadLocal.get();
        String userId = userDetail.getSys_user_id();
        String linkUserId = userDetail.getLinkUserId();
        Map<String,Object> para = new HashMap<>();
        para.put("userId",userId);
        para.put("linkUserId", (StringUtils.isNotBlank(linkUserId)?linkUserId:""));
        para.put("yearMonth", yearMonth);
        Map<String, Object> map = accountMapper.selectSumMoneyOneMonth(para);
        return Result.me().success(map);
    }

    /**
     * 统计支出占比
     *
     * @param yearMonth
     * @return
     */
    @Override
    public Result getStaticsProportion(String yearMonth) {
        UserDetail userDetail = UserTheadLocal.get();
        String userId = userDetail.getSys_user_id();
        String linkUserId = userDetail.getLinkUserId();
        Map<String,Object> para = new HashMap<>();
        para.put("userId",userId);
        para.put("linkUserId", (StringUtils.isNotBlank(linkUserId)?linkUserId:""));
        para.put("yearMonth", yearMonth);
        List<Map<String, Object>> list = accountMapper.selectStaticsProportion(para);
        List<Map<String, Object>> result = list.stream().map(map -> {
            map.put("value", map.get("money").toString());
            map.put("name", map.get("categories").toString() + " " + map.get("money").toString());
            return map;
        }).sorted( (x,y) ->{
            Double xvalue = Double.parseDouble(x.get("value").toString());
            Double yvalue = Double.parseDouble(y.get("value").toString());
            return yvalue.compareTo(xvalue);
        }).collect(Collectors.toList());

        return Result.me().success().setData(result);
    }

    /**
     * 按天统计
     *
     * @param yearMonth
     * @return
     */
    @Override
    public Result getStaticsEachDay(String yearMonth) {
        UserDetail userDetail = UserTheadLocal.get();
        String userId = userDetail.getSys_user_id();
        String linkUserId = userDetail.getLinkUserId();
        Map<String,Object> para = new HashMap<>();
        para.put("userId",userId);
        para.put("linkUserId", (StringUtils.isNotBlank(linkUserId)?linkUserId:""));
        para.put("yearMonth", yearMonth);
        List<Map<String, Object>> list = accountMapper.selectStaticsEachDay(para);
        List<String> days = new LinkedList<>();
        List<Double> money = new LinkedList<>();
        list.stream().sorted( (x,y) ->{
            Integer xDay = Integer.parseInt(x.get("day").toString());
            Integer yDay = Integer.parseInt(y.get("day").toString());
            return xDay.compareTo(yDay);
        }).forEach( i -> {
            days.add(i.get("day").toString());
            money.add(Double.parseDouble(i.get("money").toString()));
        });
        Map<String,Object> result = new HashMap<>();
        result.put("days",days);
        result.put("money",money);
        return Result.me().success().setData(result);
    }
}
