package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.talk.TalkDTO;
import com.fly.dto.talk.TalkMangeDTO;
import com.fly.service.TalkService;
import com.fly.util.Result;
import com.fly.vo.talk.TalkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.REMOVE;
import static com.fly.enums.OperationLogEum.SAVE_OR_UPDATE;

/**
 * @author Milk
 */
@Tag(name = "说说模块")
@Validated
@RestController
public class TalkController {

    @Resource
    private TalkService talkService;

    @Operation(summary = "查看后台说说列表")
    @GetMapping("/admin/talks")
    public Result<PageDTO<TalkMangeDTO>> viewAdminTalks(@RequestParam(required = false) Integer status){
        return Result.ok(talkService.listAdminTalks(status));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改说说")
    @PostMapping("/admin/talks")
    public Result<?> saveOrUpdateTalk(@Valid @RequestBody TalkVO talkVO){
        talkService.saveOrUpdateTalk(talkVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除说说")
    @DeleteMapping("/admin/talks")
    public Result<?> removeTalks(@NotEmpty @RequestBody List<Integer> talkIdList) {
        talkService.removeTalks(talkIdList);
        return Result.ok();
    }

    @Operation(summary = "获取待修改说说数据")
    @GetMapping("admin/talks/{talkId}")
    public Result<TalkMangeDTO>getModifyingAdminTalk(@NotNull @PathVariable Integer talkId){
        return Result.ok(talkService.getAdminTalk(talkId));
    }

    @Operation(summary = "前台查看所有说说")
    @GetMapping("/talks")
    public Result<PageDTO<TalkDTO>> viewTalks(){
        return Result.ok(talkService.listTalks());
    }

     @Operation(summary = "前台首页查看说说")
    @GetMapping("/home/talks")
    public Result<List<String>> viewHomePageTalks(){
        return Result.ok(talkService.listHomePageTalks());
     }
}
