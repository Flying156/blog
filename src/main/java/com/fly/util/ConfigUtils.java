package com.fly.util;


import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.service.BlogInfoService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.function.Function;

/**
 * 网站配置工具类
 *
 * @author Milk
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigUtils {

    private static BlogInfoService blogInfoService;

    @Autowired
    public void setBlogInfoService(BlogInfoService blogInfoService) {
        ConfigUtils.blogInfoService = blogInfoService;
    }

    /**
     * 获取缓存中的网站配置信息
     *
     * @param function getter 方法引用
     * @return 配置信息
     */
    @NotNull
    public static<T> T getCache(@NotNull Function<WebsiteConfig, T> function){
        T  t = function.apply(blogInfoService.getWebSiteConfig());
        if(t == null){
            throw new RuntimeException("缺少网站配置信息");
        }
        return t;
    }
}
