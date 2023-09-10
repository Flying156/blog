package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.userInfo.UserOnlineDTO;
import com.fly.service.UserInfoService;
import com.fly.util.Result;
import com.fly.vo.user.UserDisableVO;
import com.fly.vo.user.UserInfoVO;
import com.fly.vo.user.UserRoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.fly.enums.OperationLogEum.*;


/**
 * 对用户信息进行控制
 * @author Milk
 */
@Tag(name = "用户账号模块")
@RestController
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户禁用状态")
    @PutMapping("/admin/users/disable")
    public Result<?> disableUser(@Valid @RequestBody UserDisableVO userDisableVO){
        userInfoService.disableUser(userDisableVO);
        return Result.ok();
    }

    @Operation(summary = "查看在线用户")
    @GetMapping("/admin/users/online")
    public Result<PageDTO<UserOnlineDTO>> viewOnlineUsers(@RequestParam(required = false) String keywords){
        return Result.ok(userInfoService.listOnlineUsers(keywords));
    }

    @Operation(summary = "下线用户")
    @DeleteMapping("/admin/users/{userInfoId}/online")
    public Result<?> makeUserOffLine(@NotNull @PathVariable Integer userInfoId){
        userInfoService.makeUserOffLine(userInfoId);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户信息")
    @PutMapping("/users/info")
    public Result<?> updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO){
        userInfoService.updateUserInfo(userInfoVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户昵称或角色")
    @PutMapping("/admin/users/role")
    public Result<?> updateNicknameOrRole(@Valid @RequestBody UserRoleVO userRoleVO) {
        userInfoService.updateNicknameOrRole(userRoleVO);
        return Result.ok();
    }

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传用户头像")
    @PostMapping("/users/avatar")
    public Result<String> uploadUserAvatar
            (@NotNull @RequestParam("file") MultipartFile multipartFile){
        return Result.ok(userInfoService.uploadUserAvatar(multipartFile));
    }

}
