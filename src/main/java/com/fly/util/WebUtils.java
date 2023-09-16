package com.fly.util;


import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

/**
 * Web 工具类
 * @author Milk
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebUtils {

    /**
     * IP 信息搜索器
     */
    private static Ip2regionSearcher regionSearcher;


    @Autowired
    public void setRegionSearcher(Ip2regionSearcher regionSearcher) {
        WebUtils.regionSearcher = regionSearcher;
    }


    /**
     * 将自定义内容加入到请求头中，相应JSON字符串
     * @param response 返回给前端的请求
     * @param result  返回的数据
     */
    public static void renderJson(@NotNull HttpServletResponse response,
                                  @NotNull Object result) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String content = JSON.toJSONString(result, JSONWriter.Feature.FieldBased);
        response.getWriter().write(content);
    }

    /**
     * 获取 HTTPServletRequest, 如果没有返回null
     * @return 当前 HTTP 请求
     */
    public static HttpServletRequest getCurrentRequest(){
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    /**
     * 获取当前的 HTTP 请求
     * @return HTTP 会话
     */
    public static HttpSession getCurrentSession(){
        return (HttpSession) RequestContextHolder.currentRequestAttributes()
                .resolveReference(RequestAttributes.REFERENCE_SESSION);
    }

    /**
     * 获取请求浏览器的IP地址
     */
    public static String getIpAddress(@NotNull HttpServletRequest request){
        return ServletUtil.getClientIP(request);
    }

    /**
     * 获取当前 HTTP 请求的 IP 地址
     * @return IP地址
     */
    public static String getCurrentIpAddress(){
        return ServletUtil.getClientIP(getCurrentRequest());
    }

    /**
     * 获取 IP 所属地址
     * @param ipAddress IP 地址
     * @return IP 来源
     */
    @Nullable
    public static String getIpSource(@NotNull String ipAddress){
        return regionSearcher.getAddress(ipAddress);
    }

    /**
     * 获取当前 HTTP 请求的 IP 来源
     * @return IP 来源
     */
    @Nullable
    public static String getCurrentIpSource(@NotNull String ipAddress){
        return regionSearcher.getAddress(getCurrentIpAddress());
    }

    /**
     * 获取当前会话的浏览器
     * @return 会话的浏览器名称和版本
     */
    public static String getBrowserName(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        Browser browser = ua.getBrowser();
        return browser.getName() + "-" + browser.getVersion(userAgent);
    }

    /**
     * 获取当前会话的操作系统
     * @return 会话的操作系统
     */
    public static String getOsName(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        OperatingSystem os = ua.getOperatingSystem();
        return os.getName();
    }

    /**
     * 获取指定的 IP 信息
     *
     * @param ipAddress IP 地址
     * @param function  方法引用
     * @return {@link IpInfo} 的信息
     */
    public static String getInfo(@NotNull String ipAddress, @NotNull Function<IpInfo, String> function){
        return regionSearcher.getInfo(ipAddress, function);
    }
}
