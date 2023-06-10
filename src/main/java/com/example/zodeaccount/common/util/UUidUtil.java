package com.example.zodeaccount.common.util;

import java.util.UUID;

public class UUidUtil {

    /**
     * 生成32位UUID 即：不包含字符-的UUID
     *
     * @return
     * @param
     * @return String
     */
    public static String getUUId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
