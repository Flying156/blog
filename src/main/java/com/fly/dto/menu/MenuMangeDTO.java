package com.fly.dto.menu;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuMangeDTO {

    /**
     * 菜单 ID
     */
    private Integer id;

    /**
     * 菜单目录 ID
     */
    private Integer parentId;

    /**
     * 菜单名
     */
    private String name;

    /**
     * 路径
     */
    private String path;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 菜单 ICON
     */
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;
    // unused
    /**
     * 是否禁用
     */
    private Integer isDisable;

    /**
     * 是否隐藏（0 否 1 是）
     */
    private Integer isHidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子菜单列表
     */
    @Schema
    private List<MenuMangeDTO> children;

}
