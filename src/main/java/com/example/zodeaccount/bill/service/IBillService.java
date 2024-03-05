package com.example.zodeaccount.bill.service;

import com.example.zodeaccount.common.pojo.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface IBillService {

    /**
     * 首页获取记账;列表数据 (一个月)
     * @param yearMonth
     * @return
     */
    Result getMyBillData(String yearMonth);

    /**
     * 首页获取记账;头部总金额
     * @param yearMonth
     * @return
     */
    Result getMyBillMoney(String yearMonth);

    /**
     * 添加一笔支出
     * @param para
     * @return
     */
    Result addBill(Map<String,String> para);

    /**
     * 删除一笔支出
     * @param para
     * @return
     */
    Result delBill(Map<String,String> para);

    /**
     * 统计月支出
     * @param yearMonth
     * @return
     */
    Result getStatics(String yearMonth);

    /**
     * 统计支出占比
     * @param yearMonth
     * @return
     */
    Result getStaticsProportion(String yearMonth);

    /**
     * 按天统计
     * @param yearMonth
     * @return
     */
    Result getStaticsEachDay(String yearMonth);

    /**
     * 获取当前日期
     * @return
     */
    Result getCurrentDate();

    /**
     * 查出前十个金额最大账单
     * @return
     */
    Result getBillByRank(String yearMonth);

    /**
     * 按月统计
     * @param yearMonth
     * @return
     */
    Result getStaticsEachMonth(String yearMonth);

    /**
     * 获取一个分类下的账单数据
     * @param yearMonth
     * @param typeId
     * @return
     */
    Result getBillByType(String yearMonth,String typeId);
}
