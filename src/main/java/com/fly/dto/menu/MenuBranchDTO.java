package com.fly.dto.menu;


import com.fly.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuBranchDTO {

    /**
     * 子菜单页面
     */
    private List<Menu> menuBranchList;
    /**
     *  菜单分支ID和子菜单的映射关系
     */
    private Map<Integer, List<Menu>> branchIdSubmenuMap;
}
