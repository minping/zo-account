package com.example.zodeaccount;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.example.zodeaccount.common.mapper")
@ComponentScan({"com.example","com.jfinal"})
public class ZodeAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZodeAccountApplication.class, args);
        System.out.println("启动成功(●'◡'●)");
    }

}
