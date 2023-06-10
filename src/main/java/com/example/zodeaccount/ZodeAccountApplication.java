package com.example.zodeaccount;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.example.zodeaccount.common.mapper")
public class ZodeAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZodeAccountApplication.class, args);
        System.out.println("启动成功(●'◡'●)");
    }

}
