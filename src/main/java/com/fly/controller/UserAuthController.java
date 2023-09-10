package com.fly.controller;


import com.fly.annotation.AccessLimit;
import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.userAuth.AreaCountDTO;
import com.fly.dto.userAuth.UserDTO;
import com.fly.service.UserAuthService;
import com.fly.util.Result;
import com.fly.vo.user.PasswordVO;
import com.fly.vo.user.UserAuthVO;
import com.fly.vo.user.UserSearchVO;
import com.sun.istack.internal.NotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.fly.enums.OperationLogEum.UPDATE;

/**
 * @author Milk
 */
@Tag(name = "用户账号模块")
@Validated
@RestController
public class UserAuthController {

    @Resource
    private UserAuthService userAuthService;

    @Operation(summary = "浏览用户地域分布")
    @GetMapping("/admin/users/area")
    public Result<List<AreaCountDTO>> browseUserAreas(@NotNull @RequestParam("type") Integer userType){
        return Result.ok(userAuthService.listUserAreas(userType));
    }

    @Operation(summary = "查看用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<UserDTO>> viewUserList(UserSearchVO userSearchVO){
        return Result.ok(userAuthService.listUsers(userSearchVO));
    }


    @OperatingLog(type = UPDATE)
    @Operation(summary = "后台修改密码")
    @PutMapping("/admin/users/password")
    public Result<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return Result.ok();
    }

    @AccessLimit(seconds = 60, maxCount = 1)
    @Operation(summary = "发送验证码")
    @GetMapping("/users/code")
    public Result<?> sendVerificationCode(@RequestParam("username") String email){
        userAuthService.sendVerificationCode(email);
        return Result.ok();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserAuthVO userAuthVO){
        userAuthService.register(userAuthVO);
        return Result.ok();
    }

}
