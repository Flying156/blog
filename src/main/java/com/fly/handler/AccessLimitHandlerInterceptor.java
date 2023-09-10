package com.fly.handler;

import com.fly.annotation.AccessLimit;
import com.fly.enums.ResultEnum;
import com.fly.util.RedisUtils;
import com.fly.util.Result;
import com.fly.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.fly.constant.GenericConst.ONE_L;

/**
 * 接口限流拦截器
 * @author Milk
 */
@Component
public class AccessLimitHandlerInterceptor implements HandlerInterceptor{

    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                // 获取接口的访问最大次数和其访问时间
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                // 获取key，对 IP 地址进行限流
                String key = WebUtils.getIpAddress(request) + ((HandlerMethod) handler).getMethod();

                Long count = RedisUtils.incr(key);
                // 第一次访问时，加上该 IP 地址的访问时间
                if (count.equals(ONE_L)) {
                    RedisUtils.expire(key, seconds, TimeUnit.SECONDS);

                }
                if (count > maxCount) {
                    WebUtils.renderJson(response, Result.of(ResultEnum.REQUEST_BLOCKED));
                    return false;
                }
            }
        }
        return true;

    }

}

