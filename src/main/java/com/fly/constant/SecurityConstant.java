package com.fly.constant;

/**
 * Spring Security 常量
 * @author Milk
 */
public abstract class SecurityConstant {

    /**
     * Spring Security 过滤器链忽略的 URI
     */
    public static final String[] IGNORING_PATTENS = {
            // OpenAPI 文档
            "/doc.html",
            "/v3/api-docs/*",
            "/webjars/**"
    };

    /**
     * 登录处理 URL
     */
    public static final String LOGIN_PROCESSING_URL = "/login";

    /**
     * 注销 URL
     */
    public static final String LOGOUT_URL = "/logout";

    /**
     * 用户最大会话数
     */
    public static final Integer MAXIMUM_SESSIONS = 10;

}