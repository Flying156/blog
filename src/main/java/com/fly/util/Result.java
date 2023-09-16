package com.fly.util;


import com.fly.enums.ResultEnum;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import static com.fly.enums.ResultEnum.FAILURE;
import static com.fly.enums.ResultEnum.SUCCESS;
import static java.lang.Boolean.FALSE;


/**
 * 响应结果
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    /**
     * 状态标识
     */
   private Boolean flag;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    private Result(ResultEnum resultEnum) {
        BeanUtils.copyProperties(resultEnum, this);
    }

    /**
     * 获取枚举对应的响应结果
     *
     * @param resultEnum 结果枚举
     * @return 响应结果
     */
    @NotNull
    public static Result<?> of(@NotNull ResultEnum resultEnum) {
        return new Result<>(resultEnum);
    }

    /**
     * 获取成功的响应结果
     *
     * @return 响应结果
     */
    @NotNull
    public static Result<?> ok() {
        return new Result<>(SUCCESS);
    }

    /**
     * 获取带有数据的成功响应结果
     *
     * @return 响应结果
     */
    @NotNull
    public static <T> Result<T> ok(@NotNull T data) {
        Result<T> result = new Result<>(SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 获取失败的响应结果
     *
     * @return 响应结果
     */
    @NotNull
    public static Result<?> error() {
        return new Result<>(FAILURE);
    }

    /**
     * 获取带有异常信息的的失败响应结果
     *
     * @return 响应结果
     */
    @NotNull
    public static Result<?> error(@NotNull Exception exception) {
        return Result.builder()
                .flag(FALSE)
                .code(FAILURE.getCode())
                .message(exception.getMessage())
                .build();
    }

    /**
     * 获取带有信息的的失败响应结果
     *
     * @return 响应结果
     */
    @NotNull
    public static Result<?> error(@NotNull String message) {
        return Result.builder()
                .flag(FALSE)
                .code(FAILURE.getCode())
                .message(message)
                .build();
    }

}

