package com.fly.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.dto.comment.EmailDTO;
import com.fly.util.*;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.fly.constant.GenericConst.ONE;
import static com.fly.constant.GenericConst.TRUE_OF_INT;

/**
 * 全局异常处理
 *
 * @author Milk
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(value = ServiceException.class)
    public Result<?> serviceException(HttpServletRequest request, ServiceException exception){
        Throwable cause = exception.getCause();
        if(cause != null){
            // 有原因的服务异常可能包含预期外的错误
            tryNotify(cause);
            log.warn("服务异常原因: ", cause);
            log.warn("服务异常 URI: {}", request.getRequestURI());
        }
        return Result.error(exception);
    }


    /**
     * 通知异常消息
     */
    public static void tryNotify(Throwable throwable){
        if(ConfigUtils.getCache(WebsiteConfig::getNotifyError).equals(TRUE_OF_INT)){
            AsyncUtils.runAsync(() ->{
                EmailDTO emailDTO = EmailDTO.builder()
                        .email(ConfigUtils.getCache(WebsiteConfig::getEmail))
                        .subject(String.format("博客异常: %s", TimeUtils.format(TimeUtils.now())))
                        .content(ExceptionUtil.stacktraceToString(throwable, -ONE))
                        .build();
                RabbitMQUtils.sendEmail(emailDTO);
            });
        }
    }
}
