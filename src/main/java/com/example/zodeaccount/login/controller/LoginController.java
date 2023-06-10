package com.example.zodeaccount.login.controller;

import com.example.zodeaccount.handler.AuthIgnore;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.login.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    ILoginService iLoginService;

    @AuthIgnore
    @PostMapping("/authLogin")
    public Result authLogin(@RequestBody Map<String,Object> param){
        String openId = param.get("openId").toString();
        String sessionId = param.get("sessionId").toString();
        System.out.println("openId=>"+openId+",sessionId=>"+sessionId);
        return iLoginService.authLogin(openId,sessionId);
    }

}
