package com.fly.config;


import com.fly.handler.CustomizedFastJsonRedisSerializer;
import com.fly.property.SessionProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.annotation.Resource;

/**
 * 将会话交给中间件中存储，而不是服务器内存中
 * 与 Spring Security 整合，实现管理会话功能，限制用户的最大会话数
 * 当用户权限发生变动时，需要将与用户关联的会话全部关闭，防止用户的权限滥用
 *
 * @author Milk
 */

@Slf4j
@Configuration
@EnableRedisHttpSession
public class SpringSessionConfig {

    @Resource
    private SessionProperty property;

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(property.getCookieName());
        serializer.setCookieMaxAge(property.getCookieMaxAge());
        // 环境配置好在搞域名
        serializer.setDomainName(property.getDomainName());
        serializer.setCookiePath(property.getCookiePath());
        return serializer;
    }

    /**
     * Fast Json 序列化
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        // acceptNames 中是不支持持 FastJson autoType 的类，需要按照字段名匹配
        String [] acceptNames = {
                "org.springframework.security.core.context.SecurityContextImpl",
                "org.springframework.security.authentication.UsernamePasswordAuthenticationToken",
                "org.springframework.security.web.authentication.WebAuthenticationDetails",
                "org.springframework.security.web.savedrequest.DefaultSavedRequest"
        };

        return new CustomizedFastJsonRedisSerializer(acceptNames);
    }

}
