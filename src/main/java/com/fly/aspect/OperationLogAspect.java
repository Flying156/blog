package com.fly.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fly.annotation.OperatingLog;
import com.fly.entity.OperationLog;
import com.fly.entity.UserDetail;
import com.fly.mapper.OperationLogMapper;
import com.fly.util.SecurityUtils;
import com.fly.util.WebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 * @author Milk
 */
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 绑定注解
     */
    @Pointcut("@annotation(com.fly.annotation.OperatingLog)")
    public void operatingLog(){
    }

    /**
     * 声明注解功能方法
     */
    @AfterReturning(pointcut = "operatingLog()", returning = "result")
    public void saveOperationLog(JoinPoint joinPoint, Object result){
        // 获取方法签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法
        Method method = signature.getMethod();
        Class<?> declaringType = signature.getDeclaringType();
        Tag tag = declaringType.getAnnotation(Tag.class);
        Operation operation = method.getAnnotation(Operation.class);
        OperatingLog operatingLog = method.getAnnotation(OperatingLog.class);
        // 获取请求信息和用户信息
        HttpServletRequest request = WebUtils.getCurrentRequest();
        // 获取当前的用户信息
        UserDetail userDetail = SecurityUtils.getUserDetail();


        OperationLog operationLog = OperationLog.builder()
                .optModule(tag.name())
                .optType(operatingLog.type().getValue())
                .optUrl(request.getRequestURI())
                .optMethod(method.getName())
                .optDesc(operation.summary())
                .requestParam(JSON.toJSONString(joinPoint.getArgs(), JSONWriter.Feature.FieldBased))
                .requestMethod(request.getMethod())
                .responseData(JSON.toJSONString(result))
                .userId(userDetail.getId())
                .nickname(userDetail.getNickname())
                .ipAddress(userDetail.getIpAddress())
                .ipSource(userDetail.getIpSource())
                .build();

        operationLogMapper.insert(operationLog);
    }
}
