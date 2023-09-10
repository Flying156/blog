package com.fly.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author Milk
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    /**
     * 单位时间（秒）
     */
    int seconds();

    /**
     * 单位时间最大请求次数
     */
    int maxCount();
}
