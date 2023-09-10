package com.fly.config;

import com.fly.security.BlogAuthorizationManager;
import com.fly.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.annotation.Resource;

import static com.fly.constant.SecurityConstant.*;

/**
 * SpringSecurity配置类
 * @author Milk
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    /**
     * 自定义密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 会话注册表
     */
    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }



    /**
     * Spring Security 会话事件发布者
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    @Bean
    public WebSecurityCustomizer ignoringCustomizer(){
        return (web) -> web.ignoring().mvcMatchers(IGNORING_PATTENS);
    }


    @Configuration
    public static class SecurityFilterChainConfig {
        // region 认证
        @Resource
        private AuthenticationSuccessHandler authenticationSuccessHandler;

        @Resource
        private AuthenticationFailureHandler authenticationFailureHandler;

        @Resource
        private LogoutSuccessHandler logoutSuccessHandler;
        // endregion

        // region 授权
        @Resource
        private BlogAuthorizationManager authorizationManager;

        @Resource
        private AuthenticationEntryPoint authenticationEntryPoint;

        @Resource
        private AccessDeniedHandler accessDeniedHandler;
        // endregion

        @Resource
        private SessionRegistry sessionRegistry;



        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // 关闭CSRF防护
            http.csrf().disable();

            // 配置表单登录
            http.formLogin()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler);

            // 配置授权管理器
            http.authorizeHttpRequests()
                    .anyRequest()
                    .access(authorizationManager);
            // 配置登出
            http.logout()
                    .logoutUrl(LOGOUT_URL)
                    .logoutSuccessHandler(logoutSuccessHandler);

            // 配置认证异常和授权异常处理
            http.exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler);


            // 配置会话管理
            http.sessionManagement()
                    .invalidSessionUrl(LOGIN_PROCESSING_URL)
                    .maximumSessions(MAXIMUM_SESSIONS)
                    .sessionRegistry(sessionRegistry);


            return http.build();
        }


    }
}
