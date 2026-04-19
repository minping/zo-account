package com.example.zodeaccount.file.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhoump
 * @date 2026/4/16
 * @purpose
 */
public class SecurityUtils {

    // 定义路径遍历模式的正则表达式[3](@ref)[6](@ref)[7](@ref)
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
            // 经典上级目录模式 (e.g., ../, ..\)
            "(?i)(?:^|[\\\\/]|\\.\\.)[\\\\/]?\\.\\.[\\\\/]|" +
                    // URL编码的路径遍历 (e.g., %2e%2e%2f, %2e%2e/)
                    "(?:%2e|%2E|%2f|%2F|%5c|%5C)(?:%2e|%2E|%2f|%2F|%5c|%5C)|" +
                    // 绝对路径模式 (e.g., /etc/passwd, C:\boot.ini)
                    "^(?:[A-Za-z]:)?[\\\\/](?:[^\\\\/]+[\\\\/])*|" +
                    // 空字节注入 (常用于绕过检查)
                    "\\0|" +
                    // 替代分隔符和编码绕过
                    "(?:(?:\\\\|/|%2f|%2F|%5c|%5C)\\.\\.(?:\\\\|/|%2f|%2F|%5c|%5C))"
    );

    /**
     * 检测字符串中是否包含路径遍历模式[3](@ref)[6](@ref)[7](@ref)
     * @param input 待检测的输入字符串
     * @return 如果检测到路径遍历模式返回 true，否则返回 false
     */
    public static boolean containsPathTraversal(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        // 检查明显的路径遍历模式
        Matcher matcher = PATH_TRAVERSAL_PATTERN.matcher(input);
        if (matcher.find()) {
            return true;
        }

        // 检查URL双重编码绕过尝试
        try {
            String decoded = urlDecodeRecursively(input);
            if (!decoded.equals(input)) {
                matcher = PATH_TRAVERSAL_PATTERN.matcher(decoded);
                if (matcher.find()) {
                    return true;
                }
            }
        }catch (Exception e){
            return true;
        }
        return false;
    }

    /**
     * 递归解码URL编码字符串直到无更多编码[7](@ref)
     * @param input 输入字符串
     * @return 完全解码后的字符串
     */
    private static String urlDecodeRecursively(String input) throws Exception {
        String previous;
        String current = input;

        do {
            previous = current;
            current = URLDecoder.decode(previous, String.valueOf(StandardCharsets.UTF_8));
        } while (!current.equals(previous) && containsUrlEncoded(current));

        return current;
    }

    /**
     * 检查字符串是否包含URL编码字符
     * @param input 输入字符串
     * @return 如果包含URL编码返回 true
     */
    private static boolean containsUrlEncoded(String input) {
        return Pattern.compile("%[0-9A-Fa-f]{2}").matcher(input).find();
    }

    /**
     * 安全的文件路径处理[7](@ref)[8](@ref)
     * @param baseDir 基础安全目录
     * @param userInput 用户输入的文件路径
     * @return 规范化的安全路径，如果路径不安全返回 null
     */
    public static String safePathResolver(String baseDir, String userInput) {
        if (containsPathTraversal(userInput)) {
            return null;
        }

        try {
            // 规范化路径
            java.nio.file.Path basePath = java.nio.file.Paths.get(baseDir).toAbsolutePath().normalize();
            java.nio.file.Path userPath = java.nio.file.Paths.get(userInput).normalize();
            java.nio.file.Path resolvedPath = basePath.resolve(userPath).normalize();

            // 确保解析后的路径仍在基础目录内
            if (!resolvedPath.startsWith(basePath)) {
                return null;
            }

            return resolvedPath.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
