package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.comment.CommentAdminDTO;
import com.fly.dto.comment.CommentDTO;
import com.fly.service.CommentService;
import com.fly.util.Result;
import com.fly.vo.comment.CommentSearchVO;
import com.fly.vo.comment.CommentVO;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.comment.ViewCommentVO;
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
import static com.fly.enums.OperationLogEum.UPDATE;

/**
 * @author Milk
 */
@Tag(name = "评论模块")
@Validated
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "后台查看评论列表")
    @GetMapping("/admin/comments")
    public Result<PageDTO<CommentAdminDTO>> reviewCommentManagement(@Valid CommentSearchVO commentSearchVO){
        return Result.ok(commentService.listAdminComment(commentSearchVO));
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "批量删除")
    @DeleteMapping("/admin/comments")
    public Result<?> removeComments(@RequestBody @NotEmpty List<Integer> commentIdList){
        commentService.removeComments(commentIdList);
        return Result.ok();
    }

    @Operation(summary = "前台查看评论")
    @GetMapping("/comments")
    public Result<PageDTO<CommentDTO>> viewComments(@Valid ViewCommentVO viewCommentVO) {
        return Result.ok(commentService.listComments(viewCommentVO));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/comments")
    public Result<?> comment(@Valid @RequestBody CommentVO commentVO){
        commentService.saveComment(commentVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "审核评论")
    @PutMapping("/admin/comments/review")
    public Result<?> reviewComment(@Valid @RequestBody ReviewVO reviewVO){
        commentService.reviewComment(reviewVO);
        return Result.ok();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/comments/{commentId}/like")
    public Result<?> likeComment(@NotNull @PathVariable Integer commentId) {
        commentService.likeComment(commentId);
        return Result.ok();
    }
}
