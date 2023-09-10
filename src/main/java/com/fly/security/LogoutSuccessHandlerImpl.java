package com.fly.security;

import com.fly.util.Result;
import com.fly.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 注销成功处理程序
 *
 * @author Milk
 */
@Slf4j
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {



    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled() && authentication != null) {
            Object principal = authentication.getPrincipal();
            log.debug("LogoutSuccessHandler Principal: {}", principal);
        }

        WebUtils.renderJson(response, Result.ok());

    }
}
