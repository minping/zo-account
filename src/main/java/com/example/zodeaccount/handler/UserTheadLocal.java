package com.example.zodeaccount.handler;

import com.example.zodeaccount.common.pojo.UserDetail;

public class UserTheadLocal {

    private static final ThreadLocal<UserDetail> LOCAL = new ThreadLocal<>();

    public static void put(UserDetail userDetail){
        LOCAL.set(userDetail);
    }

    public static UserDetail get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }

}
