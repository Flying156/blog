package com.fly.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMenuDTO {

    /**
     * 组件名称
     */
    private String name;
    /**
     * 菜单路径
     */
    private String path;
    /**
     * 在前端的组件
     */
    private String component;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 是否隐藏
     */
    private Boolean hidden;
    /**
     * 子组件
     */
    private List<UserMenuDTO> children;
}
