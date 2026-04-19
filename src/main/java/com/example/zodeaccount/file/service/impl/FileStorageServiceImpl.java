package com.example.zodeaccount.file.service.impl;

import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.common.util.StrKit;
import com.example.zodeaccount.file.service.FileStorageService;
import com.example.zodeaccount.file.util.FileTypeDetector;
import com.example.zodeaccount.file.util.FileUtils;
import com.jfinal.model.ZoFile;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import okio.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @author zhoump
 * @date 2026/4/16
 * @purpose
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${zode.file.path}")
    private String baseDir;

    @Autowired
    FileTypeDetector  fileTypeDetector;
    @Autowired
    private FileUtils fileUtils;

    @Override
    public Result uploadFile(MultipartFile file) {
        try {
            // 文件校验
            fileUtils.validateFile(file);
            // 计算MD5
            String md5Hash = fileUtils.calculateMd5(file);
            Record ex = Db.findById("zo_file", "md5_hash", md5Hash);
            if (ex != null) {
                return Result.me().success().setData(ex);
            }
            // 生成存储文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension =  fileUtils.getFileExtension(originalFilename);
            String storageName =  fileUtils.generateStorageName(fileExtension);
            Path filePath = Paths.get(baseDir, storageName);

            // 确保目录存在
            Files.createDirectories(filePath.getParent());

            // 存储文件
            file.transferTo(filePath.toFile());

            Record zoFile = new Record();
            zoFile.set("zo_file_id",StrKit.uuid());
            zoFile.set("created_time",new Date());
            zoFile.set("updated_time",new Date());
            zoFile.set("upload_time",new Date());
            zoFile.set("original_name",originalFilename);
            zoFile.set("storage_name",storageName);
            zoFile.set("file_path",filePath.toString());
            zoFile.set("file_size",file.getSize());
            zoFile.set("file_type",file.getContentType());
            zoFile.set("file_extension",fileExtension);
            zoFile.set("md5_hash",md5Hash);
            Db.save("zo_file",zoFile);
            return Result.me().success().setData(file);
        }catch (Exception e){
            return Result.me().error().setMessage("上传文件失败");
        }
    }

    @Override
    public Resource downloadFile(String fileId, HttpServletResponse response) throws  IOException{
        Record file = Db.findById("zo_file", "zo_file_id", fileId);

        // 更新下载次数
        Record update = new Record();
        update.set("download_count",file.getLong("download_count") + 1);
        update.set("zo_file_id",fileId);
        Db.update("zo_file", "zo_file_id", update);

        // 设置响应头
        response.setContentType(file.getStr("file_type"));
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(file.getStr("original_name"), "UTF-8") + "\"");
        response.setContentLengthLong(file.getLong("file_size"));

        // 返回文件资源
        Path filePath = Paths.get(file.getStr("file_path"));
        return new UrlResource(filePath.toUri());
    }
}
