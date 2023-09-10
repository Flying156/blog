package com.fly.controller;


import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.category.CategoryMangeDTO;
import com.fly.dto.category.CategoryOptionDTO;
import com.fly.service.CategoryService;
import com.fly.util.Result;
import com.fly.vo.category.CategoryVO;
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
 * 博客信息控制器
 * @author Milk
 */
@RestController
@Validated
@Tag(name = "文章分类")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/admin/categories")
    @Operation(summary = "获取分类列表")
    public Result<PageDTO<CategoryMangeDTO>> reviewCategoryManagement(@RequestParam(required = false) String keywords){
        return Result.ok(categoryService.listAdminCategories(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @PostMapping("/admin/categories")
    @Operation(summary = "修改或新增分类")
    public Result<?> saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO){
        categoryService.saveOrUpdateCategory(categoryVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @DeleteMapping("/admin/categories")
    @Operation(summary = "批量删除分类")
    public Result<?> removeCategories(@NotEmpty @RequestBody List<Integer>categoryIdList){
        categoryService.removeCategories(categoryIdList);
        return Result.ok();
    }

    @GetMapping("/admin/categories/search")
    @Operation(summary = "后台文章列表获取文章分类")
    public Result<List<CategoryOptionDTO>> getAdminArticleCategories(){
        return Result.ok(categoryService.getAdminArticleCategories());
    }

    @Operation(summary = "前台查看文章分类")
    @GetMapping("/categories")
    public Result<PageDTO<CategoryDTO>> listCategories(){
        return Result.ok(categoryService.listCategories());
    }
}
