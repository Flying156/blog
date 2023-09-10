package com.fly.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户搜素实体类
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchVO {


    /**
     * 关键词
     */
    @Schema(description = "搜索内容")
    private String keywords;

    /**
     * 登录类型
     */
    @Schema(description = "登录类型")
    private Integer loginType;
}
