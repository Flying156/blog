package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.menu.MenuMangeDTO;
import com.fly.dto.menu.UserMenuDTO;
import com.fly.dto.role.RoleMenuDTO;
import com.fly.entity.Menu;
import com.fly.vo.menu.MenuHiddenVO;
import com.fly.vo.menu.MenuVO;

import java.util.List;

/**
 * @author Milk
 */
public interface MenuService extends IService<Menu> {
    /**
     * 获得相应的菜单数据
     */
    List<UserMenuDTO> listUserMenu();

    /**
     * 获取角色菜单数据
     */
    List<RoleMenuDTO> listRoleMenus();

    /**
     * 管理端后台菜单数据
     */
    List<MenuMangeDTO> listMenus(String keywords);

    /**
     * 修改或新增菜单
     * @param menuVO 菜单数据
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 批量删除菜单
     * @param menuId 菜单 ID
     */
    void removeMenus(Integer menuId);

    /**
     * 修改菜单隐藏状态
     * @param menuHiddenVO 修改的菜单数据
     */
    void updateHiddenStatus(MenuHiddenVO menuHiddenVO);
}
