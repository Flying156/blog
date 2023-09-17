package com.fly.property;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 会话的相关配置
 *
 * @author Milk
 */
@Data
@Component
//@ConfigurationProperties(prefix = "blog.session")
public class SessionProperty {

    /**
     * 会话 Cookie 名（默认为 SESSION)
     */
    private String cookieName = "SESSION";

    /**
     * 域名（当前域名或更高级的域名，测试环境不要设置域名！）
     */
    private String domainName = "flyingbullt.top";

    /**
     * 会话作用域（默认为根目录）
     */
    private String cookiePath = "/";

    /**
     * 会话过期时间（单位秒，默认为 30 分钟）
     */
    private Integer cookieMaxAge = 30 * 60;

}
