package com.example.zodeaccount.bill.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Day2Bill {

    private String day;
    private String sumMoney;
    List<Map<String,Object>> bills;
}
