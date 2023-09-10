package com.fly.util;

import com.fly.property.QiNiuProperty;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 七牛云上传工具
 *
 * @author Milk
 */
@Component
@EnableConfigurationProperties(QiNiuProperty.class)
public class QiNiuUtils {
    @Resource
    private QiNiuProperty qiNiuProperty;


    public String uploadImage(MultipartFile file){
        // 获取唯一名称
        String filename = FileIoUtils.getRandomFileName(file);

        // 密钥配置
        Auth auth = Auth.create(qiNiuProperty.getAccessKey(), qiNiuProperty.getSecretKey());
        // 构造一个带Zone对象的配置类
        Configuration configuration = new Configuration(Region.huadong());

        UploadManager uploadManager = new UploadManager(configuration);

        try {
            byte[] imageBytes = file.getBytes();

            String upToken = auth.uploadToken(qiNiuProperty.getBucket());
            Response response = uploadManager.put(imageBytes, filename, upToken);
            // 解析上传成功结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            // 获取外链地址
            return qiNiuProperty.getDomainOfBucket() + "/" + putRet.key;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
