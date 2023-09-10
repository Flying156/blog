package com.fly.strategy.impl;

import com.fly.enums.FileExtensionEnum;
import com.fly.property.UploadProperty;
import com.fly.strategy.UploadStrategy;
import com.fly.util.FileIoUtils;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;



/**
 * 文件上传
 *
 * @author Milk
 */
@Component
@EnableConfigurationProperties(UploadProperty.class)
public class UploadStrategyImpl implements UploadStrategy {

    private UploadProperty uploadProperty;

    @Autowired
    private void setUploadProperty(UploadProperty uploadProperty){
        this.uploadProperty = uploadProperty;
    }

    /**
     * 上传文件
     * @param file         文件
     * @param directoryUri 存储的目录
     * @return  文件访问路径
     */
    @Override
    public String uploadFile(MultipartFile file, String directoryUri) {
        try(InputStream inputStream = file.getInputStream()){
            String fileName = FileIoUtils.getRandomFileName(file);
            // 文件名根据 MD5 生成，每个文件唯一
            String fileUri = directoryUri + fileName;
            if(!exists(fileUri)){
                upload(file, inputStream, directoryUri, fileName);
            }
            return getFileAccessUrl(fileUri);
        }catch(Exception cause){
            throw new ServiceException("文件上传失败", cause);
        }
    }

    /**
     * 通过文件输入流上传
     * @param inputStream  文件输入流
     * @param directoryUri 上传文件目录 URI
     * @param fileName     文件名（带扩展名）
     * @return  文件路径
     */
    @Override
    public String uploadFile(InputStream inputStream, String directoryUri, String fileName) {
        try{
            String fileUri = directoryUri + fileName;
            // 不检测 exist, fileUri 重复但是内容不同
            upload(null, inputStream, directoryUri, fileName);
            return getFileAccessUrl(fileUri);
        }catch (Exception cause){
            throw new ServiceException("文件上传失败", cause);
        }
    }

    /**
     * 判断文件是否存在
     * @param fileUri 文件 Uri
     */
    private boolean exists(String fileUri){
        String fileUrl = uploadProperty.getUploadUrl() + fileUri;
        return new File(fileUrl).exists();
    }

    /**
     * 获取文件访问路径
     * @param fileUri 文件目录
     * @return 文件访问路径
     */
    private String getFileAccessUrl(String fileUri) {
        return uploadProperty.getAccessUrl() + fileUri;
    }

    /**
     * 上传
     * @param multipartFile 文件
     * @param inputStream   输入流
     * @param directoryUri  文件目录
     * @param fileName      文件名称
     */
    private void upload(MultipartFile multipartFile, InputStream inputStream, String directoryUri, String fileName) throws IOException {
        // 首先判断目录是否存在
        String directoryUrl = uploadProperty.getUploadUrl() + directoryUri;
        File directory = new File(directoryUrl);
        if(!directory.exists() && !directory.mkdirs()){
            throw new RuntimeException("目录创建失败");
        }
        File file = new File(directoryUrl + fileName);
        FileExtensionEnum fileExtensionEnum = FileExtensionEnum.get
                (FileIoUtils.getExtensionName(fileName));
        switch (fileExtensionEnum){
            case MD:
            case TXT:
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {
                    FileIoUtils.copyCharStream(reader, writer);
                }
                break;
            default:
                try(BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()))){
                    FileIoUtils.copyByteStream(bis, bos);
                }
        }


    }
}
