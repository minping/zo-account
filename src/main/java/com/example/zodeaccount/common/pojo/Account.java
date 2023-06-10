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
@TableName("sys_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 3149820030972518676L;

    @TableId(value = "sys_account_id")
    private String sys_account_id;

    private String sys_user_id;

    private Date create_time;

    private Long money;

    private String description;

    private String del_flag;

    private String msg_id;

    private String categories;

    private String typeId;

}
