package com.fly.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fly.constant.RedisConst;
import com.fly.entity.UserDetail;
import com.fly.security.BlogAuthorizationManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static com.fly.constant.GenericConst.*;

/**
 * @author Milk
 */
@Slf4j
@Component
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    private static PasswordEncoder passwordEncoder;

    private static BlogAuthorizationManager authorizationManager;

    private static SessionRegistry sessionRegistry;

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry){
        SecurityUtils.sessionRegistry = sessionRegistry;
    }

    @Autowired
    public void setAuthorizationManager(BlogAuthorizationManager authorizationManager){
        SecurityUtils.authorizationManager = authorizationManager;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        SecurityUtils.passwordEncoder = passwordEncoder;
    }
    /**
     * 获取唯一的名称
     * @return 名称
     */
    @NotNull
    public static String getUniqueName(){
        return  "name_id:" + IdWorker.getIdStr();
    }


    /**
     * 清除 Spring Security 的授权凭据
     */
    public static void clearAuthorizationCredentials(){
        authorizationManager.updateAuthorizationCredentials();
    }


    /**
     * 获取所有在线用户的用户名
     * @return  用户名的集合
     */
    public static Set<String> getOnlineUsernames(){
        return ConvertUtils.castSet(RedisUtils.sMembers(RedisConst.ONLINE_USERNAME));
    }

    /**
     * 获取当前 Spring Security 上下文存储的用户信息
     * <p>
     *     SecurityContextHolder 是 Spring Security 存储 认证 用户细节的地方。
     *     存储 SecurityContext，从 SecurityContext 中可以获得 Authentication
     *     principal: 识别用户。当用用户名/密码进行认证时，这通常是 UserDetails 的一个实例。
     *     credentials: 通常是一个密码。在许多情况下，这在用户被认证后被清除，以确保它不会被泄露。
     *     authorities: GrantedAuthority 实例是用户被授予的高级权限。两个例子是角色（role）和作用域（scope）。
     * </p>
     * @return 用户信息
     */
    @NotNull
    public static UserDetail getUserDetail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof UsernamePasswordAuthenticationToken){
            Object principal = authentication.getPrincipal();
            if(principal instanceof UserDetail){
                return (UserDetail) principal;
            }
        }
        String requestUri = Optional.ofNullable(WebUtils.getCurrentRequest())
                .map(HttpServletRequest::getRequestURI).orElse("null");
        log.warn("The user is not logged on, but the path {} requested", requestUri);
        throw new ServiceException("用户未登录");
    }

    /**
     * 获取当前 Spring Security 上下文用户的指定信息
     *
     * @param function 方法引用
     * @return {@link UserDetail} 的信息
     */
    public static <T> T getInfo(Function<UserDetail, T> function){
        return function.apply(getUserDetail());
    }

    /**
     * 获取当前 Spring Security 上下文的用户信息 ID
     *
     * @return 用户信息 ID
     */
    @NotNull
    public static Integer getUserInfoId(){
        return getInfo(UserDetail::getUserInfoId);
    }


    /**
     * 获取指定主体的所有已知会话，不会返回已经销毁的会话，可能会返回已经过期的会话
     *
     * @param principal              指定的主体
     * @param includeExpiredSessions 是否返回过期的会话
     * @return   主体匹配的会话
     */
    @NotNull
    public static List<SessionInformation> getAllSession(@NotNull Object principal, boolean includeExpiredSessions){
        return sessionRegistry.getAllSessions(principal, includeExpiredSessions);
    }

    /**
     * 获取指定主体的所有已知会话，不会返回已经过期的会话
     *
     * @param principal 查找会话的主体
     * @return  此主体匹配的未过期会话
     */
    public static List<SessionInformation> getNonExpiredSessions(@NotNull Object principal) {
        return getAllSession(principal, false);
    }


    /**
     * 判断主体是否没有未过期会话（通过主体的 username 判断）。
     *
     * @param principal 查找会话的主体（不应为 <code>null<code>）
     * @return 如果主体没有其他未过期会话，返回 true；否则返回 false。
     */
    public static boolean noOtherSessions(Object principal){
        return getNonExpiredSessions(principal).isEmpty();
    }

    /**
     * 判断主体是否有未过期会话（通过主体的 username 判断）。
     *
     * @param principal 查找会话的主体
     * @return 如果主体有其他未过期会话，返回 true；否则返回 false。
     */
    public static boolean hasOtherSessions(Object principal){
        return !noOtherSessions(principal);
    }


    /**
     * 匹配用户的密码是否和数据库的密码是否相同
     * @param oldPassword  输入的旧密码
     * @param password     数据库的密码
     * @return             是否匹配
     */
    public static boolean matches(String oldPassword, String password) {
        return passwordEncoder.matches(oldPassword, password);
    }


    /**
     * 将传输的字符串加密
     * @param newPassword 新密码
     * @return 加密后的密码
     */
    public static String encode(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }


    /**
     * 获取随机验证码
     *
     * @return 验证码
     */
    public static String getRandomCode(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();
        // 6 位随机数字
        for(int i = ZERO; i < SIX; i++){
            stringBuilder.append(random.nextInt(TEN));
        }
        return stringBuilder.toString();
    }
}
