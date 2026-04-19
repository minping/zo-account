package com.example.zodeaccount.file.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/4/16
 * @purpose
 */
@Component
@Slf4j
public class FileTypeDetector {

    @Value("${file.upload.allowed-types:}")
    private String[] allowedFileTypes;

    // 文件头魔数字典（HEX格式）
    private static final Map<String, String> FILE_TYPE_MAGIC_NUMBERS = new HashMap<>();

    static {
        // 图片类型
        FILE_TYPE_MAGIC_NUMBERS.put("FFD8FF", "image/jpeg");
        FILE_TYPE_MAGIC_NUMBERS.put("89504E47", "image/png");
        FILE_TYPE_MAGIC_NUMBERS.put("47494638", "image/gif");
        FILE_TYPE_MAGIC_NUMBERS.put("49492A00", "image/tiff");
        FILE_TYPE_MAGIC_NUMBERS.put("424D", "image/bmp");

        // 文档类型
        FILE_TYPE_MAGIC_NUMBERS.put("25504446", "application/pdf");
        FILE_TYPE_MAGIC_NUMBERS.put("D0CF11E0", "application/msword"); // DOC/XLS/PPT
        FILE_TYPE_MAGIC_NUMBERS.put("504B0304", "application/zip"); // ZIP/Office Open XML

        // 文本类型
        FILE_TYPE_MAGIC_NUMBERS.put("EFBBBF", "text/plain; charset=utf-8"); // UTF-8 with BOM
        FILE_TYPE_MAGIC_NUMBERS.put("FFFE", "text/plain; charset=utf-16le"); // UTF-16 LE
        FILE_TYPE_MAGIC_NUMBERS.put("FEFF", "text/plain; charset=utf-16be"); // UTF-16 BE
        FILE_TYPE_MAGIC_NUMBERS.put("3C3F786D6C", "application/xml"); // XML
        FILE_TYPE_MAGIC_NUMBERS.put("68746D6C3E", "text/html"); // HTML

        // 压缩文件
        FILE_TYPE_MAGIC_NUMBERS.put("52617221", "application/x-rar-compressed");
        FILE_TYPE_MAGIC_NUMBERS.put("1F8B08", "application/gzip");

        // 音频视频
        FILE_TYPE_MAGIC_NUMBERS.put("57415645", "audio/wav");
        FILE_TYPE_MAGIC_NUMBERS.put("41564920", "video/x-msvideo");
        FILE_TYPE_MAGIC_NUMBERS.put("000001BA", "video/mpeg");
    }

    private static final Tika tika = new Tika();

    /**
     * 检测文件内容类型（结合文件头和Tika分析）
     * @param file 上传的文件
     * @return 真实的MIME类型
     * @throws IOException 文件读取异常
     */
    public String detectContentType(MultipartFile file) throws IOException {
        // 优先通过文件头魔数检测
        String magicNumberType = detectByMagicNumber(file);
        if (magicNumberType != null && !magicNumberType.equals("application/octet-stream")) {
            return magicNumberType;
        }

        // 文件头无法确定时使用Tika深度检测
        return detectByTika(file);
    }

    /**
     * 通过文件头魔数检测文件类型[1](@ref)
     * @param file 上传的文件
     * @return MIME类型或null
     * @throws IOException 文件读取异常
     */
    private String detectByMagicNumber(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            // 读取文件前20字节用于魔数检测
            byte[] header = new byte[20];
            int read = is.read(header, 0, header.length);

            if (read < 3) return "application/octet-stream";

            // 将前20字节转换为十六进制字符串
            StringBuilder hexBuilder = new StringBuilder();
            for (int i = 0; i < read; i++) {
                hexBuilder.append(String.format("%02X", header[i]));
            }
            String hexHeader = hexBuilder.toString().toUpperCase();

            // 检查所有已知魔数
            for (Map.Entry<String, String> entry : FILE_TYPE_MAGIC_NUMBERS.entrySet()) {
                String magicNumber = entry.getKey();
                if (hexHeader.startsWith(magicNumber)) {
                    return entry.getValue();
                }
            }

            return "application/octet-stream";
        }
    }

    /**
     * 使用Apache Tika检测文件类型[6](@ref)[7](@ref)
     * @param file 上传的文件
     * @return MIME类型
     * @throws IOException 文件读取异常
     */
    private String detectByTika(MultipartFile file) throws IOException {
        return tika.detect(file.getInputStream());
    }

    /**
     * 验证文件类型是否允许[8](@ref)
     * @param file 上传的文件
     * @param allowedTypes 允许的MIME类型数组
     * @return 是否允许
     * @throws IOException 文件读取异常
     */
    public boolean isAllowedFileType(MultipartFile file, String[] allowedTypes)
            throws IOException {
        if (allowedTypes == null || allowedTypes.length == 0) {
            return true;
        }

        String detectedType = detectContentType(file);
        return Arrays.asList(allowedTypes).contains(detectedType);
    }

    /**
     * 获取文件扩展名对应的常见MIME类型
     * @param filename 文件名
     * @return MIME类型
     */
    public String getMimeTypeByExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "application/octet-stream";
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            case "pdf": return "application/pdf";
            case "doc": return "application/msword";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls": return "application/vnd.ms-excel";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            case "txt": return "text/plain";
            case "html": case "htm": return "text/html";
            case "xml": return "application/xml";
            case "mp3": return "audio/mpeg";
            case "mp4": return "video/mp4";
            default: return "application/octet-stream";
        }
    }
}
