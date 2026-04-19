package com.example.zodeaccount.file.service;

import com.example.zodeaccount.common.pojo.Result;
import com.jfinal.model.ZoFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author zhoump
 * @date 2026/4/16
 * @purpose
 */
public interface FileStorageService {

    Result uploadFile(MultipartFile file);

    Resource downloadFile(String filename, HttpServletResponse response) throws IOException, FileNotFoundException;
}
