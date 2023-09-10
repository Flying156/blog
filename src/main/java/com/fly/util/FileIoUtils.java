package com.fly.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.fly.constant.FilePathConst.DOT;

/**
 * 文件 IO 工具类
 *
 * @author Milk
 */
public abstract class FileIoUtils {
    /**
     * 返回文件十六进制 MD5 摘要的文件名
     * @param multipartFile 文件
     * @return 新文件名
     */
    public static String getRandomFileName(@NotNull MultipartFile multipartFile){
        String md5DigestAsHex;
        try (InputStream inputStream = multipartFile.getInputStream()){
            // 这里已经计算过内容的 MD5 值，可以在上传时进行 Base64 编码并加入到上传请求中，但改动太大
            md5DigestAsHex = DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException cause) {
            throw new RuntimeException("获取新文件名异常", cause);
        }
        String extensionName = getExtensionName(multipartFile.getOriginalFilename());
        return md5DigestAsHex + DOT + extensionName;
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带 "."
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getExtensionName(String fileName) {
        return FileNameUtil.getSuffix(fileName);
    }


    /**
     * 关闭可关闭的输入输出流
     */
    public static void close(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception cause) {
                throw new ServiceException("关闭对象异常", cause);
            }
        }
    }

    /**
     * 拷贝输入流
     * @param reader 字符输入流
     * @param writer 字符输出流
     */
    public static void copyCharStream(BufferedReader reader, BufferedWriter writer) {
        IoUtil.copy(reader, writer);
    }

    /**
     * 拷贝输入流
     * @param bis 字节输入流
     * @param bos 字节输出流
     */
    public static void copyByteStream(BufferedInputStream bis, BufferedOutputStream bos) {
        IoUtil.copy(bis, bos);
    }

    /**
     * 获取主要文件名（去除文件名后缀）
     * @param fileName 文件初始名
     * @return 文件名
     */
    public static String getMainName(String fileName) {
        return FileNameUtil.mainName(fileName);
    }

    /**
     * 从文件读取内容，转换为字节数组
     * @param inputStream 输入流
     * @return 内容字节数组
     */
    public static byte[] readBytes(InputStream inputStream) {
        return IoUtil.readBytes(inputStream);
    }

    /**
     * 按行读取文章
     * @param inputStream 文章输入流
     * @return 文章每行数据
     */
    public static List<String> readLines(InputStream inputStream) {
        return IoUtil.readLines(inputStream, StandardCharsets.UTF_8, new ArrayList<>());
    }
}
