package com.fly.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色展示实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleMangeDTO {

    /**
     * 角色 ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色标签
     */
    private String roleLabel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否禁用
     */
    private Integer isDisable;

    /**
     * 资源 ID 列表
     */
    @Schema
    private List<Integer> resourceIdList;

    /**
     * 菜单 ID 列表
     */
    @Schema
    private List<Integer> menuIdList;
}
