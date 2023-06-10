package com.example.zodeaccount.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zodeaccount.common.pojo.User;
import com.example.zodeaccount.common.pojo.UserDetail;

import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 查找用户通过公纵号的用户openid
     * @param openId
     * @return
     */
    Map<String, Object> selectUserByOpenId(String openId);

    /**
     * 查找用户通过小程序的用户openid
     * @param openId
     * @return
     */
    UserDetail selectUserByMiniAppOpenId(String openId);
}
