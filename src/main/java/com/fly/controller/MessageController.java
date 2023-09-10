package com.fly.controller;

import com.fly.annotation.AccessLimit;
import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.message.MessageAdminDTO;
import com.fly.dto.message.MessageDTO;
import com.fly.service.MessageService;
import com.fly.util.Result;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.message.MessageSearchVO;
import com.fly.vo.message.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.fly.enums.OperationLogEum.REMOVE;
import static com.fly.enums.OperationLogEum.UPDATE;

/**
 * 消息管理
 *
 * @author Milk
 */
@Tag(name = "消息模块")
@Validated
@RestController
public class MessageController {

    @Resource
    private MessageService messageService;

    @Operation(summary = "查看后台留言列表")
    @GetMapping("/admin/messages")
    public Result<PageDTO<MessageAdminDTO>> listMessageAdmin(@Valid MessageSearchVO messageSearchVO){
        return Result.ok(messageService.listMessageAdmin(messageSearchVO));
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result<?> removeMessages(@RequestBody @NotEmpty List<Integer> messageIdList){
        messageService.removeMessages(messageIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "审核留言")
    @PutMapping("admin/messages/review")
    public Result<?> reviewMessages(@RequestBody @Valid ReviewVO reviewVO){
        messageService.reviewMessages(reviewVO);
        return Result.ok();
    }

    @Operation(summary = "前台查看留言")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages(){
        return Result.ok(messageService.listMessage());
    }

    @AccessLimit(seconds = 30, maxCount = 1)
    @Operation(summary = "发送留言")
    @PostMapping("/messages")
    public Result<?> sendMessage(@Valid @RequestBody MessageVO messageVO){
        messageService.saveMessage(messageVO);
        return Result.ok();
    }

}
