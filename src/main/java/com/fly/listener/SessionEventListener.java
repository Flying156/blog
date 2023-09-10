package com.fly.listener;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fly.entity.UserDetail;
import com.fly.util.RedisUtils;
import com.fly.util.SecurityUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.fly.constant.RedisConst.ONLINE_USERNAME;

/**
 * Spring Security 会话事件监听器
 *
 * @author Milk
 */
@Component
public class SessionEventListener {

    /**
     * 会话销毁时移除用于会话控制的用户信息
     */
    @Async
    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent destroyedEvent){
        //
        List<String> principalNames = destroyedEvent
                .getSecurityContexts()
                .stream()
                .map(securityContext -> (UserDetail) securityContext
                        .getAuthentication().getPrincipal())
                // 确保用户没有其他会话
                .filter(SecurityUtils::noOtherSessions)
                .map(UserDetail::getUsername)
                .collect(Collectors.toList());
        // 删除下线的用户
        if(CollectionUtils.isNotEmpty(principalNames)){
            RedisUtils.sRemove(ONLINE_USERNAME, principalNames.toArray());
        }
    }
}
