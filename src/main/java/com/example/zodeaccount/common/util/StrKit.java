package com.example.zodeaccount.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class StrKit {
    public static final String UTF8 = "UTF-8";
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
    public static final String RE_EXCEL_START_MARK = "^[\\n\\t@\\-=+]";

    public StrKit() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String randomStr(int strLength) {
        if (strLength <= 0) {
            return "";
        } else {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < strLength; ++i) {
                int index = random.nextInt(characters.length());
                sb.append(characters.charAt(index));
            }

            return sb.toString();
        }
    }

    public static String md5(String str) {
        byte[] secretBytes = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException("没有md5这个算法！");
        }

        String md5code = (new BigInteger(1, secretBytes)).toString(16);
        int length = md5code.length();

        for(int i = 0; i < 32 - length; ++i) {
            md5code = "0" + md5code;
        }

        return md5code;
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] + 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] = (char)(arr[0] - 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String leftFill(String str, int length, String fillStr) {
        return fill(str, length, fillStr, true);
    }

    public static String rightFill(String str, int length, String fillStr) {
        return fill(str, length, fillStr, false);
    }

    public static String fill(String str, int length, String fillStr, boolean isLeft) {
        if (str == null) {
            return null;
        } else {
            int strLength = str.length();

            StringBuffer sb;
            for(sb = new StringBuffer(); strLength < length; ++strLength) {
                sb.append(fillStr);
            }

            return isLeft ? sb.toString() + str : str + sb.toString();
        }
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        } else {
            int len = str.length();
            if (len == 0) {
                return true;
            } else {
                int i = 0;

                while(i < len) {
                    switch (str.charAt(i)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            ++i;
                            break;
                        default:
                            return false;
                    }
                }

                return true;
            }
        }
    }

    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    public static String toCamelCase(String stringWithUnderline) {
        if (stringWithUnderline.indexOf(95) == -1) {
            return stringWithUnderline;
        } else {
            stringWithUnderline = stringWithUnderline.toLowerCase();
            char[] fromArray = stringWithUnderline.toCharArray();
            char[] toArray = new char[fromArray.length];
            int j = 0;

            for(int i = 0; i < fromArray.length; ++i) {
                if (fromArray[i] == '_') {
                    ++i;
                    if (i < fromArray.length) {
                        toArray[j++] = Character.toUpperCase(fromArray[i]);
                    }
                } else {
                    toArray[j++] = fromArray[i];
                }
            }

            return new String(toArray, 0, j);
        }
    }

    public static String join(String delimiter, Object... objs) {
        return join(delimiter, true, objs);
    }

    public static String join(String delimiter, boolean ignore, Object... objs) {
        if (objs != null && 0 != objs.length) {
            StringBuffer bf = new StringBuffer();
            boolean flag = true;

            for(int i = 0; i < objs.length; ++i) {
                Object obj = objs[i];
                if (obj != null && !isBlank(obj.toString()) || !ignore) {
                    if (flag) {
                        bf.append(obj);
                        flag = false;
                    } else {
                        bf.append(delimiter).append(obj);
                    }
                }
            }

            return bf.toString();
        } else {
            return "";
        }
    }

    public static String join(String[] stringArray) {
        StringBuilder sb = new StringBuilder();
        String[] var2 = stringArray;
        int var3 = stringArray.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            sb.append(s);
        }

        return sb.toString();
    }

    public static String join(String[] stringArray, String separator) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < stringArray.length; ++i) {
            if (i > 0) {
                sb.append(separator);
            }

            sb.append(stringArray[i]);
        }

        return sb.toString();
    }

    public static String join(Collection<String> lStrs, String separator) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = lStrs.iterator();

        for(int i = 0; iterator.hasNext(); sb.append((String)iterator.next())) {
            if (i++ > 0) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }

    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    public static String base64Decoder(String base64Str) {
        return new String(Base64.getDecoder().decode(base64Str));
    }

    public static String base64Encoder(String base64Str) {
        try {
            return new String(Base64.getEncoder().encode(base64Str.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException var2) {
            UnsupportedEncodingException e = var2;
            e.printStackTrace();
            return null;
        }
    }

    public static String format(String strPattern, Object... argArray) {
        if (isNotBlank(strPattern) && argArray != null && argArray.length != 0) {
            int strPatternLength = strPattern.length();
            StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
            int handledPosition = 0;

            for(int argIndex = 0; argIndex < argArray.length; ++argIndex) {
                int delimIndex = strPattern.indexOf("{}", handledPosition);
                if (delimIndex == -1) {
                    if (handledPosition == 0) {
                        return strPattern;
                    }

                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }

                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == '\\') {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == '\\') {
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(argArray[argIndex]);
                        handledPosition = delimIndex + 2;
                    } else {
                        --argIndex;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append('{');
                        handledPosition = delimIndex + 1;
                    }
                } else {
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(argArray[argIndex]);
                    handledPosition = delimIndex + 2;
                }
            }

            sbuf.append(strPattern, handledPosition, strPattern.length());
            return sbuf.toString();
        } else {
            return strPattern;
        }
    }

    public static String urlEncode(String content, String enc) {
        if (isBlank(content)) {
            return content;
        } else {
            try {
                return URLEncoder.encode(content, enc);
            } catch (UnsupportedEncodingException var3) {
                UnsupportedEncodingException e = var3;
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static String urlEncode(String content) {
        return urlEncode(content, "UTF-8");
    }

    public static String urlDecode(String content) {
        return urlDecode(content, "UTF-8");
    }

    public static String urlDecode(String content, String enc) {
        try {
            content = URLDecoder.decode(content, enc);
        } catch (UnsupportedEncodingException var3) {
            UnsupportedEncodingException e = var3;
            e.printStackTrace();
        }

        return content;
    }

    public static boolean isNumber(String sStr) {
        try {
            double var1 = Double.valueOf(sStr);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str != null && suffix != null) {
            if (suffix.length() > str.length()) {
                return false;
            } else {
                int strOffset = str.length() - suffix.length();
                return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
            }
        } else {
            return str == null && suffix == null;
        }
    }

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();

            for(int i = 0; i < messageDigest.length; ++i) {
                String shaHex = Integer.toHexString(messageDigest[i] & 255);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }

                hexString.append(shaHex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException var6) {
            NoSuchAlgorithmException e = var6;
            e.printStackTrace();
            return "";
        }
    }

    public static String trimSeparator(String str, String separator) {
        while(str.startsWith(separator)) {
            str = str.substring(str.indexOf(separator) + 1);
        }

        while(str.endsWith(separator)) {
            str = str.substring(0, str.lastIndexOf(separator));
        }

        return str;
    }

    public static String trimAllBlank(String str) {
        if (str == null) {
            return null;
        } else {
            char[] chars = str.toCharArray();
            int oldLength = chars.length;
            int index = 0;
            int newLength = 0;

            while(newLength < oldLength) {
                char aChar = chars[newLength];
                switch (aChar) {
                    default:
                        chars[index++] = aChar;
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                        ++newLength;
                }
            }

            newLength = index;
            if (oldLength == newLength) {
                return str;
            } else {
                char[] newChars = new char[newLength];
                System.arraycopy(chars, 0, newChars, 0, newLength);
                return new String(newChars);
            }
        }
    }

    public static String cleanHtmlTag(String content) {
        if (content == null) {
            return null;
        } else {
            String result = content.replaceAll("(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)", "");
            return replaceFirst(result);
        }
    }

    public static String replaceFirst(String str) {
        return replaceFirst(str, "*");
    }

    public static String replaceFirst(String str, String replacement) {
        return str == null ? null : str.replaceFirst("^[\\n\\t@\\-=+]", replacement);
    }
}
