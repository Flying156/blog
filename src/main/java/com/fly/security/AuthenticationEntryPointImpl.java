package com.fly.security;

import com.fly.enums.ResultEnum;
import com.fly.util.Result;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 身份验证入口点
 * 如果用户没有被认证，或者应用程序抛出 AuthenticationException，那么进入
 * 此类启动身份验证方案。
 * @author Milk
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        if(log.isDebugEnabled()){
            String requestUri = request.getRequestURI();
            log.debug("AuthenticationEntryPoint URI:{}", requestUri);
        }
        WebUtils.renderJson(response, Result.of(ResultEnum.AUTHENTICATION_REQUIRED));
    }
}
