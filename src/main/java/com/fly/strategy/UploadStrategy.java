package com.fly.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件上传策略
 *
 * @author Milk
 */
public interface UploadStrategy {


     /**
      * 上传或下载文件
      * @param file         文件
      * @param directoryUri 存储的目录
      * @return   文件访问 URL
      */
     String uploadFile(MultipartFile file, String directoryUri);


     /**
      * 上传或下载文件
      *
      * @param inputStream  文件输入流
      * @param directoryUri 上传文件目录 URI
      * @param fileName      文件名（带扩展名）
      * @return   文件访问 URL
      */
     String uploadFile(InputStream inputStream, String directoryUri, String fileName);
}
