package com.fly.security;

import com.fly.constant.RedisConst;
import com.fly.dto.auth.UserDetailDTO;
import com.fly.entity.UserAuth;
import com.fly.entity.UserDetail;
import com.fly.mapper.UserAuthMapper;
import com.fly.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Milk
 */
@Slf4j
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Resource
    private UserAuthMapper userAuthMapper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (log.isDebugEnabled()) {
            Object principal = authentication.getPrincipal();
            log.debug("AuthenticationSuccessHandler Principal:\n{}", principal);
        }

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        updateLoginInfoAsync(userDetail);
        UserDetailDTO userDetailsDTO = BeanCopyUtils.copy
                (userDetail, UserDetailDTO.class);

        WebUtils.renderJson(response, Result.ok(userDetailsDTO));
    }


    private void updateLoginInfoAsync(UserDetail userDetails){
        AsyncUtils.runAsync(() -> {
                    UserAuth userAuth = UserAuth.builder()
                            .id(userDetails.getId())
                            .ipAddress(userDetails.getIpAddress())
                            .ipSource(userDetails.getIpSource())
                            .browser(userDetails.getBrowser())
                            .os(userDetails.getOs())
                            .lastLoginTime(userDetails.getLastLoginTime())
                            .build();
                    userAuthMapper.updateById(userAuth);
                    // 记录在线用户
                    RedisUtils.sAdd(RedisConst.ONLINE_USERNAME, userDetails.getUsername());
                });

    }
}
