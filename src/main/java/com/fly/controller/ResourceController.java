package com.fly.controller;


import com.fly.annotation.OperatingLog;
import com.fly.dto.resource.ResourceMangeDTO;
import com.fly.dto.resource.RoleResourceDTO;
import com.fly.service.ResourceService;
import com.fly.util.Result;
import com.fly.vo.resource.ResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.*;

/**
 * @author Milk
 */
@RestController
@Validated
@Tag(name = "资源模块")
public class ResourceController {

     @Resource
     private ResourceService resourceService;

    @GetMapping("admin/role/resources")
    @Operation(summary = "查看角色资源权限")
    public Result<List<RoleResourceDTO>>viewRoleResourceAuthority(){
        return Result.ok(resourceService.listRoleResources());
    }

    @GetMapping("/admin/resources")
    @Operation(summary = "后台查看资源列表")
    public Result<List<ResourceMangeDTO>> reviewResourceManagement(@RequestParam(required = false) String keywords) {
        return Result.ok(resourceService.listResources(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @PostMapping("/admin/resources")
    @Operation(summary = "新增或修改资源")
    public Result<?> saveOrUpdateResource(@Valid @RequestBody ResourceVO resourceVO){
        resourceService.saveOrUpdateResource(resourceVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @DeleteMapping("/admin/resources/{resourceId}")
    @Operation(summary = "删除资源")
    public Result<?> removeResources(@NotNull @PathVariable Integer resourceId){
        resourceService.removeResources(resourceId);
        return Result.ok();
    }
}
