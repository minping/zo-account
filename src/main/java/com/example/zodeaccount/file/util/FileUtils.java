package com.example.zodeaccount.file.util;

import com.example.zodeaccount.common.util.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * @author zhoump
 * @date 2026/4/17
 * @purpose
 */
@Component
public class FileUtils {


    @Value("${zode.file.maxFileSize}")
    private long maxFileSize;

    @Autowired
    FileTypeDetector  fileTypeDetector;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd");


    /**
     * 文件验证方法
     */
    public void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型安全性
        String contentType = fileTypeDetector.detectContentType(file);
        if (!fileTypeDetector.isAllowedFileType(file, new String[]{contentType})) {
            throw new IllegalArgumentException("不支持的文件类型: " + contentType);
        }

        // 检查文件名安全性
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && SecurityUtils.containsPathTraversal(originalFilename)) {
            throw new IllegalArgumentException("文件名包含非法字符");
        }
    }



    /**
     * 计算文件的MD5哈希值
     * @param file 文件对象
     * @return MD5哈希值字符串
     * @throws IOException 文件读取异常
     */
    public String calculateMd5(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;

            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 计算文件的MD5哈希值
     * @param filePath 文件路径
     * @return MD5哈希值字符串
     * @throws IOException 文件读取异常
     */
    public String calculateMd5(String filePath) {
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;

            while ((read = is.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();

            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            System.out.println("MD5算法不可用"+ e.getMessage());
            return "";
        }
    }



    /**
     * 获取文件扩展名[9](@ref)
     */
    public String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 生成存储文件名（UUID + 扩展名）
     * @param fileExtension 文件扩展名
     * @return 存储文件名
     */
    public String generateStorageName(String fileExtension) {
        String uuid = StrKit.uuid();
        if (StringUtils.hasText(fileExtension)) {
            return uuid + "." + fileExtension;
        }
        return uuid;
    }

    /**
     * 生成带日期目录的存储路径
     * @param baseDir 基础目录
     * @param fileExtension 文件扩展名
     * @return 完整存储路径
     */
    public String generateStoragePath(String baseDir, String fileExtension) {
        String dateDir = LocalDateTime.now().format(DATE_FORMATTER);
        String fileName = generateStorageName(fileExtension);

        return baseDir + File.separator + dateDir + File.separator + fileName;
    }

    /**
     * 验证文件大小是否在限制范围内
     * @param file 文件对象
     * @param maxSize 最大文件大小（字节）
     * @return 是否有效
     */
    public boolean validateFileSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

    /**
     * 验证文件类型是否在允许范围内
     * @param file 文件对象
     * @param allowedTypes 允许的文件类型数组
     * @return 是否有效
     */
    public boolean validateFileType(MultipartFile file, String[] allowedTypes) {
        if (allowedTypes == null || allowedTypes.length == 0) {
            return true;
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 创建目录（如果不存在）
     * @param dirPath 目录路径
     * @throws IOException 目录创建失败
     */
    public void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 获取文件MIME类型
     * @param file 文件对象
     * @return MIME类型
     */
    public String getMimeType(MultipartFile file) {
        return file.getContentType();
    }

    /**
     * 获取文件MIME类型
     * @param filePath 文件路径
     * @return MIME类型
     * @throws IOException 文件读取异常
     */
    public String getMimeType(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.probeContentType(path);
    }

    /**
     * 安全删除文件
     * @param filePath 文件路径
     * @return 是否成功删除
     */
    public boolean safeDeleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取文件大小（人类可读格式）
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public String getHumanReadableSize(long size) {
        if (size < 1024) {
            return size + " B";
        }

        int exp = (int) (Math.log(size) / Math.log(1024));
        String unit = "KMGTPE".charAt(exp - 1) + "B";

        return String.format("%.1f %s", size / Math.pow(1024, exp), unit);
    }

    /**
     * 复制文件
     * @param source 源文件路径
     * @param target 目标文件路径
     * @throws IOException 文件操作异常
     */
    public void copyFile(String source, String target) throws IOException {
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);

        createDirectoryIfNotExists(targetPath.getParent().toString());
        Files.copy(sourcePath, targetPath);
    }

    /**
     * 验证文件是否完整
     * @param filePath 文件路径
     * @param expectedMd5 期望的MD5值
     * @return 是否完整
     * @throws IOException 文件读取异常
     */
    public boolean verifyFileIntegrity(String filePath, String expectedMd5)
            throws IOException {
        String actualMd5 = calculateMd5(filePath);
        return actualMd5.equals(expectedMd5);
    }

}
