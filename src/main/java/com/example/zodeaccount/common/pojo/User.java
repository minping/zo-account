package com.example.zodeaccount.common.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class User implements Serializable {
    private static final long serialVersionUID = 8477083545201883003L;

    @TableId(value = "sys_user_id")
    private String sys_user_id;

    private String userName;

    private String nickName;

    private String passWord;

    private String status;

    private String email;

    private String phonenumber;

    private String gender;

    private String userType;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

    private Integer delFlag;

    private String openId;

    private String avatarUrl;

    private String miniappOpenId;

    private String linkUserId;

}
