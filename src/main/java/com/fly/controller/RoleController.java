package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.role.RoleMangeDTO;
import com.fly.dto.role.UserRoleDTO;
import com.fly.service.RoleService;
import com.fly.util.Result;
import com.fly.vo.role.DisableRoleVO;
import com.fly.vo.role.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.fly.enums.OperationLogEum.*;

/**
 * @author Milk
 */
@RestController
@Validated
@Tag(name = "角色权限管理模块")
public class RoleController {

    @Resource
    private RoleService roleService;

    @GetMapping("/admin/roles")
    @Operation(summary = "查看角色管理列表")
    public Result<PageDTO<RoleMangeDTO>> reviewRoleManagement(@RequestParam(required = false) String keywords){
        return Result.ok(roleService.listRoleManagement(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @PostMapping("/admin/role")
    @Operation(summary = "保存或更新角色")
    public Result<?> saveOrUpdateRole(@Valid @RequestBody RoleVO roleVO){
        roleService.saveOrUpdateRole(roleVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @DeleteMapping("admin/roles")
    @Operation(summary = "删除角色")
    public Result<?> removeRoles(@NotEmpty @RequestBody List<Integer> roleIdList){
        roleService.removeRoles(roleIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @PutMapping("admin/roles/disable")
    @Operation(summary = "修改角色禁用状态")
    public Result<?> updateDisableStatus(@Valid @RequestBody DisableRoleVO disableRoleVO){
        roleService.updateDisableStatus(disableRoleVO);
        return Result.ok();
    }

    @Operation(summary = "查看用户列表角色")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> viewUserRole(){
        return Result.ok(roleService.listUserRoles());
    }
}
