package com.example.zodeaccount.plugin.controller;

import com.example.zodeaccount.common.util.StrKit;
import com.example.zodeaccount.handler.AuthIgnore;
import com.example.zodeaccount.plugin.util.MarkdownToHtmlConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PlugInController {

    @AuthIgnore
    @PostMapping("parseMarkdown")
    public String parseMarkdown(String content,String key) {
        if(StrKit.isBlank(content) || StrKit.isBlank(key)){
            return "";
        }
        if(!"DreamSoft!@#@123".equals(key)){
            return "";
        }
        content = StrKit.base64Decoder(content);
        String html = MarkdownToHtmlConverter.parseMarkdown(content);
        return html;
    }

}
