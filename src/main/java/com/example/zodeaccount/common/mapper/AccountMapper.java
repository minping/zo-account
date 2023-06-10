package com.example.zodeaccount.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zodeaccount.common.pojo.Account;
import com.example.zodeaccount.wechart.pojo.Bill;

import java.util.List;
import java.util.Map;

public interface AccountMapper extends BaseMapper<Account> {

    Map<String, Object> selectTheMonthMoneyByUserId(String userId);

    List<Bill> selectTheMonthMoneyDetailByUserId(String userId);

    Map<String, Object> selectTheWeekMoneyByUserId(String userId);

    List<Bill> selectTheWeekMoneyDetailByUserId(String userId);

    Map<String, Object> selectTheDayMoneyByUserId(String userId);

    List<Bill> selectTheDayMoneyDetailByUserId(String userId);

    Map<String, Object> selectTheYearMoneyByUserId(String userId);

    List<Map<String, Object>> selectMyBillByUserId(Map<String,Object> para);

    Map<String, Object> selectSumMoneyOneMonth(Map<String,Object> para);

    List<Map<String, Object>> selectStaticsProportion(Map<String,Object> para);

    List<Map<String, Object>> selectStaticsEachDay(Map<String,Object> para);


}
