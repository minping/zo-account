package com.example.zodeaccount.plugin.service.impl;

import com.example.zodeaccount.common.util.StrKit;
import com.example.zodeaccount.file.util.FileUtils;
import com.example.zodeaccount.plugin.service.PlugInService;
import com.example.zodeaccount.plugin.util.BilibiliAudioDownloader;
import com.example.zodeaccount.plugin.util.HttpClientRedirect;
import com.example.zodeaccount.plugin.util.MarkdownToHtmlConverter;
import com.example.zodeaccount.plugin.util.RedirectChainResult;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class PlugInServiceImpl implements PlugInService {

    @Autowired
    private FileUtils fileUtils;

    @Value("${zode.server.path}")
    private String serverPath;

    @Autowired
    BilibiliAudioDownloader bilibiliAudioDownloader;

    @Override
    public String parseMarkdown(Map<String, Object> param) {
        if(!param.containsKey("content") || param.get("content") == null){
            return "参数错误";
        }
        String content = param.get("content").toString();
        try {
            content =  URLDecoder.decode(content, String.valueOf(StandardCharsets.UTF_8));
            content = StrKit.base64Decoder(content);
        }catch (Exception e){
            System.out.println("解码错误 = " + e.getMessage());
        }
        return MarkdownToHtmlConverter.parseMarkdown(content);
    }

    @Override
    public String fetchWeChatArticleCoverImage(String width, String height) {
        String apiUrl = StrKit.format("https://picsum.photos/{}/{}",width,height);
        HttpClientRedirect httpClientRedirect = new HttpClientRedirect();
        try{
            RedirectChainResult redirectChainResult = httpClientRedirect.fetchWithRedirectChain(apiUrl);
            List<String> redirectChain = redirectChainResult.getRedirectChain();
            if (redirectChain.isEmpty()){
                return "未获取到图片链接";
            }
            return redirectChain.get(redirectChain.size() - 1);
        }catch (Exception e){
            return e.getMessage();
        }
    }

    /**
     * 解析b站视频
     *
     * @param bid
     * @return
     */
    @Override
    public String bliVideoParser(String bid) {
        File audio = bilibiliAudioDownloader.downloadBilibiliAudioAsMP3(bid);
        // 生成存储文件名
        String originalFilename = audio.getName();
        String fileExtension =  fileUtils.getFileExtension(originalFilename);
        String storageName =  fileUtils.generateStorageName(fileExtension);
        // 计算MD5
        String md5Hash = fileUtils.calculateMd5(audio.getPath());
        Record ex = Db.findById("zo_file", "md5_hash", md5Hash);
        if (ex != null) {
            return serverPath+"msg/file/download/"+ex.getStr("zo_file_id");
        }
        // 保存元数据
        Record zoFile = new Record();
        zoFile.set("zo_file_id",StrKit.uuid());
        zoFile.set("created_time",new Date());
        zoFile.set("updated_time",new Date());
        zoFile.set("upload_time",new Date());
        zoFile.set("original_name",audio.getName());
        zoFile.set("storage_name",storageName);
        zoFile.set("file_path",audio.getAbsolutePath());
        zoFile.set("file_size",audio.length());
        zoFile.set("file_type","audio");
        zoFile.set("file_extension",fileExtension);
        zoFile.set("md5_hash",md5Hash);
        Db.save("zo_file",zoFile);
        return serverPath+"msg/file/download/audio/"+zoFile.getStr("zo_file_id");
    }
}
