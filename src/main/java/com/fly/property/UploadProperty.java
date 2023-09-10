package com.fly.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 上传文件属性
 *
 * @author Milk
 */
@Data
@ConfigurationProperties(prefix = "blog.local")
public class UploadProperty {

    private String uploadUrl;

    private String accessUrl;

}
