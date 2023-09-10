package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.service.PageService;
import com.fly.util.Result;
import com.fly.vo.page.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.REMOVE;
import static com.fly.enums.OperationLogEum.SAVE_OR_UPDATE;


/**
 * @author Milk
 */
@Tag(name = "页面模块")
@Validated
@RestController
public class PageController {

    @Resource
    private PageService pageService;

    @Operation(summary = "后台查看页面管理")
    @GetMapping("/admin/pages")
    public Result<List<PageVO>> reviewPageManagement(){
        return Result.ok(pageService.listPages());
    }


    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新页面")
    @PostMapping("/admin/pages")
    public Result<?> saveOrUpdatePage(@Valid @RequestBody PageVO pageVO) {
        pageService.saveOrUpdatePage(pageVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除页面")
    @DeleteMapping("/admin/pages/{pageId}")
    public Result<?> removePage(@NotNull @PathVariable Integer pageId) {
        pageService.removePage(pageId);
        return Result.ok();
    }
}
