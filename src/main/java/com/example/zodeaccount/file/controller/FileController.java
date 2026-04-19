package com.example.zodeaccount.file.controller;

import com.example.zodeaccount.common.pojo.Result;
import com.example.zodeaccount.file.service.FileStorageService;
import com.example.zodeaccount.handler.AuthIgnore;
import com.jfinal.kit.FileKit;
import okio.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author zhoump
 * @date 2026/4/16
 * @purpose
 */
@RestController
@RequestMapping("/file")
@Validated
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result uploadFile( @RequestParam("file") MultipartFile file) {
        return fileStorageService.uploadFile(file);
    }

    @GetMapping("/download/{fileId}")
    @AuthIgnore
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, HttpServletResponse response) {
        try {
            Resource resource = fileStorageService.downloadFile(fileId, response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/audio/{fileId}")
    @AuthIgnore
    public ResponseEntity<InputStreamResource> downloadAudio(@PathVariable String fileId, HttpServletResponse response) {
        try {
            Resource rf = fileStorageService.downloadFile(fileId, response);
            File file = rf.getFile();
            byte[] audioBytes = FileKit.file2Byte(file);
            // 2. 将字节数组转换为InputStreamResource
            InputStream inputStream = new ByteArrayInputStream(audioBytes);
            InputStreamResource resource = new InputStreamResource(inputStream);
            // 3. 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            // 对于动态内容，通常不设置Content-Length，或设置为准确长度
            // 4. 返回响应
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
