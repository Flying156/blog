package com.fly.controller;


import com.fly.dto.menu.MenuMangeDTO;
import com.fly.dto.menu.UserMenuDTO;
import com.fly.dto.role.RoleMenuDTO;
import com.fly.service.MenuService;
import com.fly.util.Result;
import com.fly.vo.menu.MenuHiddenVO;
import com.fly.vo.menu.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 菜单模块
 *
 * @author Milk
 */
@RestController
@Tag(name = "菜单模块")
@Validated
public class MenuController {
    @Resource
    private MenuService menuService;

    @GetMapping("/admin/user/menus")
    @Operation(summary = "浏览用户菜单")
    public Result<List<UserMenuDTO>> browseUserMenu(){
        return Result.ok(menuService.listUserMenu());
    }

    @GetMapping("/admin/role/menus")
    @Operation(summary = "查看角色菜单权限")
    public Result<List<RoleMenuDTO>> viewRoleMenuAuthority(){
        return Result.ok(menuService.listRoleMenus());
    }


    @GetMapping("/admin/menus")
    @Operation(summary = "后台查看菜单列表")
    public Result<List<MenuMangeDTO>> reviewMenuManagement(@RequestParam(required = false) String keywords){
        return Result.ok(menuService.listMenus(keywords));
    }

    @PostMapping("/admin/menus")
    @Operation(summary = "添加或修改菜单列表")
    public Result<?> saveOrUpdateMenu(@Valid @RequestBody MenuVO menuVO){
        menuService.saveOrUpdateMenu(menuVO);
        return Result.ok();
    }

    @DeleteMapping("/admin/menus/{menuId}")
    @Operation(summary = "删除菜单")
    public Result<?> removeMenus(@NotNull @PathVariable Integer menuId){
        menuService.removeMenus(menuId);
        return Result.ok();
    }

    @PutMapping("/admin/users/hidden")
    @Operation(summary = "修改菜单隐藏状态")
    public Result<?> updateHiddenStatus(@Valid @RequestBody MenuHiddenVO menuHiddenVO){
        menuService.updateHiddenStatus(menuHiddenVO);
        return Result.ok();
    }
}
