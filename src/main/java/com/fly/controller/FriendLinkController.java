package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.friendLink.FriendLinkDTO;
import com.fly.dto.friendLink.FriendLinkMangeDTO;
import com.fly.service.FriendLinkService;
import com.fly.util.Result;
import com.fly.vo.friendLink.FriendLinkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.fly.enums.OperationLogEum.REMOVE;
import static com.fly.enums.OperationLogEum.SAVE_OR_UPDATE;

/**
 * @author Milk
 */
@Tag(name = "友链模块")
@Validated
@RestController
public class FriendLinkController {
    @Resource
    private FriendLinkService friendLinkService;

    @Operation(summary = "后台查看友链列表")
    @GetMapping("/admin/links")
    public Result<PageDTO<FriendLinkMangeDTO>> listAdminFriendLinks(@RequestParam(required = false) String keywords) {
        return Result.ok(friendLinkService.listAdminFriendLinks(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改友链")
    @PostMapping("/admin/links")
    public Result<?> saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO){
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除友链")
    @DeleteMapping("/admin/links")
    public Result<?> removeFriendLink(@NotEmpty @RequestBody List<Integer> friendLinkIdList){
        friendLinkService.removeFriendLink(friendLinkIdList);
        return Result.ok();
    }

    @Operation(summary = "前台查看友链")
    @GetMapping("/links")
    public Result<List<FriendLinkDTO>> viewFriendLinks(){
        return Result.ok(friendLinkService.listFriendLinks());
    }
}
