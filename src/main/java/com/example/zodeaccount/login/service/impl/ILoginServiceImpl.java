package com.example.zodeaccount.login.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.zodeaccount.common.util.JWTUtil;
import com.example.zodeaccount.common.util.UUidUtil;
import com.example.zodeaccount.redis.RedisKey;
import com.example.zodeaccount.redis.RedisUtil;
import com.example.zodeaccount.common.mapper.UserMapper;
import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.common.pojo.User;
import com.example.zodeaccount.common.pojo.UserDetail;
import com.example.zodeaccount.login.service.ILoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ILoginServiceImpl implements ILoginService {

    /** 日志 */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 自动登录
     *
     * @param openId
     * @param sessionId
     * @return
     */
    @Override
    public Result authLogin(String openId, String sessionId) {
        // 1.判断用户是否存在 存在则查出用户,否则注册一个用户
        UserDetail userDetail = getUserByMiniAppOpenId(openId);
        // 2.得到用户后生成token
        String userId = userDetail.getSys_user_id();
        String token = JWTUtil.createJWT(userId, 24 * 60 * 1000);
        // 3.将得到的token存进redis
        redisTemplate.opsForValue().set(RedisKey.TOKEN+token, JSON.toJSONString(userDetail),1, TimeUnit.HOURS);
        // 4.返回用户信息数据
        Map<String,Object> data = new HashMap<>();
        data.put("token",token);
        data.put("userInfo",userDetail);
        return Result.me().success(data);
    }

    private UserDetail getUserByMiniAppOpenId(String openId){
        UserDetail userDetail = userMapper.selectUserByMiniAppOpenId(openId);
        if (Objects.isNull(userDetail)){
            User user = new User();
            user.setSys_user_id(UUidUtil.getUUId());
            user.setMiniappOpenId(openId);
            int insert = userMapper.insert(user);
            if (insert>0){
                logger.info("新建用户成功:"+openId);
            }
            userDetail = UserDetail.fromUser(user);
        }
        logger.info("返回用户:"+userDetail.toString());
        return userDetail;
    }
}
