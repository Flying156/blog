package com.fly.aspect;

import cn.hutool.core.date.StopWatch;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 计算程序耗时
 *
 * @author Milk
 */
@Slf4j
@Aspect
@Component
public class TimeAspect {

    /**
     * 定义切面
     * 切入包下的所有类的所以方法
     */
    @Pointcut("execution(* com.fly.controller.*.*(..))")
    public void controller(){

    }

    @Pointcut("execution(* com.fly.security.*.*(..))")
    public void security(){

    }

    @Around("controller()")
    public Object timingController(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        long timeMills = stopWatch.getLastTaskTimeMillis();
        // 获取当前请求
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String requestMethod = request.getMethod();
        String requestUri = request.getRequestURI();


        String shortSignature = joinPoint.getSignature().toShortString();
        String content = String.format("%s %s 接口调用耗时: %d ms 签名: %s",
                requestMethod, requestUri,timeMills, shortSignature);
        log.info(content);
        return result;
    }

    @Around("security()")
    public Object timingSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        long timeMills = stopWatch.getLastTaskTimeMillis();
        // 获取当前请求



        String shortSignature = joinPoint.getSignature().toShortString();
        String content = String.format("Security 方法调用耗时: %d ms 签名: %s",
                timeMills, shortSignature);
        log.info(content);
        return result;
    }


}
