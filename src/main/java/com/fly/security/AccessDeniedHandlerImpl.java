package com.fly.security;

import com.fly.enums.ResultEnum;
import com.fly.util.Result;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 授权异常处理程序
 *
 * @author Milk
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        if(log.isDebugEnabled()){
            String requestUri = request.getRequestURI();
            log.debug("AccessDeniedHandler RequestURI: {}", requestUri);
        }
        WebUtils.renderJson(response, Result.of(ResultEnum.ACCESS_DENIED));
    }
}
