package com.example.zodeaccount.wechart.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Bill implements Serializable {

    private static final long serialVersionUID = 2714600107445958527L;

    private String id;
    private String money;
    private String description;
    private String date;

}
