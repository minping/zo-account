package com.example.zodeaccount.plugin.service;

import java.util.Map;

public interface PlugInService {

    /**
     * 解析markdown
     * @param param
     * @return
     */
    String parseMarkdown(Map<String,Object> param);

    /**
     * 获取微信文章封面图片
     * @param width
     * @param height
     * @return
     */
    String fetchWeChatArticleCoverImage(String width,String  height);

    /**
     * 解析b站视频
     * @param bid
     * @return
     */
    String bliVideoParser(String bid);
}
