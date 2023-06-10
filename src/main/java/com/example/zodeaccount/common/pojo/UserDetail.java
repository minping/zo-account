package com.example.zodeaccount.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail implements Serializable {

    private static final long serialVersionUID = -2525595138879420100L;

    private String sys_user_id;
    private String nickName;
    private String passWord;
    private String gender;
    private String phonenumber;
    private String avatarUrl;
    private String miniappOpenId;
    private String openId;
    private String linkUserId;

    public static UserDetail fromUser(User user){
        UserDetail userDetail = new UserDetail();
        userDetail.setSys_user_id(user.getSys_user_id());
        userDetail.setPassWord(user.getPassWord());
        userDetail.setNickName(user.getNickName());
        userDetail.setAvatarUrl(user.getAvatarUrl());
        userDetail.setGender(user.getGender());
        userDetail.setOpenId(user.getOpenId());
        userDetail.setMiniappOpenId(user.getMiniappOpenId());
        userDetail.setLinkUserId(user.getLinkUserId());
        return userDetail;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "sys_user_id='" + sys_user_id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", gender='" + gender + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", miniappOpenId='" + miniappOpenId + '\'' +
                ", openId='" + openId + '\'' +
                ", linkUserId='" + linkUserId + '\'' +
                '}';
    }
}
