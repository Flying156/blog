package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.tag.TagDTO;
import com.fly.dto.tag.TagMangeDTO;
import com.fly.service.TagService;
import com.fly.util.Result;
import com.fly.vo.tag.TagVO;
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
@RestController
@Validated
@Tag(name = "标签管理")
public class TagController {

    @Resource
    private TagService tagService;

    @OperatingLog(type = REMOVE)
    @DeleteMapping("/admin/tags")
    @Operation(summary = "批量删除标签")
    public Result<?> removeTags(@NotEmpty @RequestBody List<Integer> tagIdList){
        tagService.removeTags(tagIdList);
        return Result.ok();
    }

    @GetMapping("/admin/tags")
    @Operation(summary = "管理端查询标签及其数量")
    public Result<PageDTO<TagMangeDTO>> browseTags(@RequestParam(required = false) String keywords){
        return Result.ok(tagService.browseTags(keywords));
    }


    @OperatingLog(type = SAVE_OR_UPDATE)
    @PostMapping("/admin/tags")
    @Operation(summary = "添加或修改标签")
    public Result<?> saveOrUpdateTag(@Valid @RequestBody TagVO tagVO){
        tagService.saveOrUpdateTag(tagVO);
        return Result.ok();
    }

    @GetMapping("/admin/tags/search")
    @Operation(summary = "后台文章列表获取文章标签")
    public Result<List<TagDTO>> getAdminArticleTags(){
        return Result.ok(tagService.listAdminArticleTags());
    }

    @GetMapping("/tags")
    @Operation(summary = "前台获取标签列表")
    public Result<PageDTO<TagDTO>> viewTagList(){
        return Result.ok(tagService.listTag());
    }
}
