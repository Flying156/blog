package com.fly.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 七牛云上传配置类
 *
 * @author Milk
 */
@Data
@ConfigurationProperties(prefix = "oss.qiniu")
public class QiNiuProperty {
    /**
     * 七牛云ACCESS_KEY
     */
    private String accessKey;

    /**
     * 七牛云SECRET_KEY
     */
    private String secretKey;

    /**
     * 七牛云空间名
     */
    private String bucket;

    /**
     * 七牛云空间域名
     */
    private String domainOfBucket;


}
