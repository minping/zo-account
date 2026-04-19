package com.example.zodeaccount.plugin.controller;

import com.example.zodeaccount.handler.PlugInAuth;
import com.example.zodeaccount.plugin.service.PlugInService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/plugin/")
public class PlugInController {

    @Resource
    PlugInService plugInService;

    @PlugInAuth
    @PostMapping("parseMarkdown")
    public String parseMarkdown(@RequestBody Map<String,Object> param) {
        return plugInService.parseMarkdown(param);
    }

    @PlugInAuth
    @GetMapping("fetchWeChatArticleCoverImage")
    public String fetchWeChatArticleCoverImage(String width,String  height){
        return plugInService.fetchWeChatArticleCoverImage(width, height);
    }

    @PlugInAuth
    @GetMapping("bliVideoParser")
    public String bliVideoParser(String bid){
        return plugInService.bliVideoParser(bid);
    }

}
