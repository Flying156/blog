package com.fly.annotation;

import com.fly.enums.OperationLogEum;

import java.lang.annotation.*;

/**
 * 运行日志注解
 * Target注解: 目标为方法
 * <p>
 *     RetentionPolicy.SOURCE : 仅存在于源代码中，编译阶段会被丢弃，不会包含于class字节码文件中。@Override, @SuppressWarnings都属于这类注解。
 *     RetentionPolicy.CLASS : 默认策略，在class字节码文件中存在，在类加载的时被丢弃，运行时无法获取到。
 *     RetentionPolicy.RUNTIME : 始终不会丢弃，可以使用反射获得该注解的信息。自定义的注解最常用的使用方式。
 * </p>
 * @author Milk
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatingLog {

    /**
     * 操作类型
     */
    OperationLogEum type();
}
