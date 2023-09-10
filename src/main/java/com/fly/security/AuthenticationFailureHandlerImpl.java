package com.fly.security;

import com.fly.util.Result;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.fly.enums.ResultEnum.*;

/**
 * 认证失败处理程序
 * @author Milk
 */

@Slf4j
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            String message = exception.getMessage();
            log.debug("AuthenticationFailureHandler Message: {}", message);
        }
        if (exception instanceof DisabledException) {
            // 用户被禁用
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_DISABLED));
        } else if (exception instanceof LockedException) {
            // 用户无可用权限角色
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_LOCKED));
        } else {
            // BadCredentialsException
            // 用户名或密码错误
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_FAILURE));
        }
    }
}
