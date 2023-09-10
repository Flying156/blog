package com.fly.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 用户禁用实体类
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDisableVO {


    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Integer id;


    @NotNull(message = "禁用状态不能为空")
    @Schema(description = "用户禁用状态")
    private Integer isDisable;
}
