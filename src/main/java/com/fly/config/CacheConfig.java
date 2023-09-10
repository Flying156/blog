package com.fly.config;

import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import static com.fly.constant.CacheConst.CACHE_NAME_PREFIX;

/**
 * 缓存配置类，配置序列化方式
 *
 * @author Milk
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存前缀
        configuration = configuration.prefixCacheNameWith(CACHE_NAME_PREFIX);
        // key 用 String 序列化
        configuration = configuration.serializeKeysWith(RedisSerializationContext
                .SerializationPair.fromSerializer(RedisSerializer.string()));
        // value 用 FastJson2 序列化
        configuration = configuration.serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
        return configuration;


    }

}
