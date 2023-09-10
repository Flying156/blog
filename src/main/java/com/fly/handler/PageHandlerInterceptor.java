package com.fly.handler;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.util.PageUtils;
import com.fly.util.StrRegexUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.fly.constant.PageConst.*;

/**
 * 分页拦截器
 */
@Component
@SuppressWarnings("all")
public class PageHandlerInterceptor implements HandlerInterceptor {
    /**
     * 查询请求头是否有current, size等字段
     * 有则存入到ThreadLocal中
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String current = request.getParameter(CURRENT);
        if (StrRegexUtils.isNotBlank(current)) {
            String size = Optional.ofNullable
                    (request.getParameter(SIZE)).orElse(DEFAULT_SIZE);
            PageUtils.setPage(Page.of(Long.parseLong(current), Long.parseLong(size)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if(PageUtils.getPage() != null){
            PageUtils.removePage();
        }
    }
}
