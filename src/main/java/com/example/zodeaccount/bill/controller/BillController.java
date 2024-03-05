package com.example.zodeaccount.bill.controller;

import com.example.zodeaccount.bill.service.IBillService;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.handler.AuthIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    IBillService iBillService;

    @GetMapping("/getMyBillData")
    public Result getMyBillData(String yearMonth){
        return iBillService.getMyBillData(yearMonth);
    }

    @GetMapping("/getMyBillMoney")
    public Result getMyBillMoney(String yearMonth){
        return iBillService.getMyBillMoney(yearMonth);
    }

    @PostMapping("/addBill")
    public Result addBill(@RequestBody Map<String,String> para){
        return iBillService.addBill(para);
    }

    @PostMapping("/delBill")
    public Result delBill(@RequestBody Map<String,String> para){
        return iBillService.delBill(para);
    }

    @GetMapping("/getStatics")
    public Result getStatics(String yearMonth){
        return iBillService.getStatics(yearMonth);
    }

    @GetMapping("/getStaticsProportion")
    public Result getStaticsProportion(String yearMonth){
        return iBillService.getStaticsProportion(yearMonth);
    }

    @GetMapping("/getStaticsEachDay")
    public Result getStaticsEachDay(String yearMonth){
        return iBillService.getStaticsEachDay(yearMonth);
    }

    @AuthIgnore
    @GetMapping("/getCurrentDate")
    public Result getCurrentDate(){
        return iBillService.getCurrentDate();
    }

    @GetMapping("/getBillByRank")
    public Result getBillByRank(String yearMonth){
        return iBillService.getBillByRank(yearMonth);
    }

    @GetMapping("/getStaticsEachMonth")
    public Result getStaticsEachMonth(String yearMonth){
        return iBillService.getStaticsEachMonth(yearMonth);
    }

    @GetMapping("/getBillByType")
    public Result getBillByType(String yearMonth,String typeId){
        return iBillService.getBillByType(yearMonth,typeId);
    }
}
