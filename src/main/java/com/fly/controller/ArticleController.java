package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.*;
import com.fly.service.ArticleService;
import com.fly.util.Result;
import com.fly.vo.article.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.*;

/**
 * @author Milk
 */
@RestController
@Validated
@Tag(name = "文章操作模块")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/admin/articles")
    @Operation(summary = "查看后台文章列表")
    public Result<PageDTO<ArticleMangeDTO>> viewAdminArticles(@Valid ArticleSearchVO articleSearchVO){
        return Result.ok(articleService.listAdminArticles(articleSearchVO));
    }

    @OperatingLog(type = REMOVE)
    @DeleteMapping("/admin/articles")
    @Operation(summary = "物理删除文章")
    public Result<?> removeArticlePhysically(@NotEmpty @RequestBody List<Integer> articleIdList) {
        articleService.removeArticlePhysically(articleIdList);
        return Result.ok();
    }


    @OperatingLog(type = UPDATE)
    @PutMapping("/admin/articles")
    @Operation(summary = "逻辑删除或恢复文章")
    public Result<?>recoverOrRemoveArticleLogically(@Valid @RequestBody TableLogicVO tableLogicVO){
        articleService.recoverOrRemoveArticleLogically(tableLogicVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @PutMapping("/admin/articles/top")
    @Operation(summary = "修改文章置顶状态")
    public Result<?> updateArticleTop(@Valid @RequestBody ArticleTopVO articleTopVO){
        articleService.updateArticleTop(articleTopVO);
        return Result.ok();
    }

    @GetMapping("/admin/articles/{articleId}")
    @Operation(summary = "根据ID查询文章")
    public Result<ArticleVO> getAdminArticle(@NotNull @PathVariable Integer articleId){
        return Result.ok(articleService.getAdminArticle(articleId));
    }

    @OperatingLog(type = UPLOAD)
    @PostMapping("/admin/articles/images")
    @Operation(summary = "上传文章封面")
    public Result<String> saveArticleCover(@NotNull @RequestParam("file") MultipartFile multipartFile){
        return Result.ok(articleService.saveArticleCover(multipartFile));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @PostMapping("/admin/articles")
    @Operation(summary = "发布或修改文章内容")
    public Result<?>saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO){
        articleService.saveOrUpdateArticle(articleVO);
        return Result.ok();
    }

    @Operation(summary = "前台获取文章")
    @GetMapping("/articles")
    public Result<List<HomePageArticleDTO>> viewHomePageArticles() {
        return Result.ok(articleService.listHomePageArticles());
    }

    @Operation(summary = "前台根据分类ID查看分类或标签文章预览")
    @GetMapping("/articles/condition")
    public Result<ArticlePreviewDTO> getArticlePreview(@Valid ArticlePreviewVO articlePreviewVO){
        return Result.ok(articleService.getArticlePreview(articlePreviewVO));
    }

    @Operation(summary = "前台查看文章内容")
    @GetMapping("/articles/{articleId}")
    public Result<ArticleDTO> getArticle(@NotNull @PathVariable Integer articleId){
        return Result.ok(articleService.getArticle(articleId));
    }

    @Operation(summary = "文章归档")
    @GetMapping("/articles/archives")
    public Result<PageDTO<ArticleArchiveDTO>> getArticleArchives(){
        return Result.ok(articleService.getArticleArchives());
    }

    @Operation(summary = "文章点赞")
    @PostMapping("/articles/{articleId}/like")
    public Result<?> likeArticle(@NotNull @PathVariable Integer articleId){
        articleService.likeArticle(articleId);
        return Result.ok();
    }

    @Operation(summary = "搜索文章")
    @GetMapping("/articles/search")
    public Result<List<ArticleSearchDTO>> searchArticle(@RequestParam(required = false) String keywords){
        return Result.ok(articleService.listArticlesBySearch(keywords));
    }

    @Operation(summary = "导出文章")
    @PostMapping("/admin/articles/export")
    public Result<List<String>> exportArticles(@NotEmpty @RequestBody List<Integer> articleIdList){
        return Result.ok(articleService.exportArticles(articleIdList));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "导入文章")
    @PostMapping("/admin/articles/import")
    public Result<?> importArticle(@NotNull @RequestParam("file") MultipartFile multipartFile,
                                   @RequestParam(name = "type", required = false) String strategyName){
        articleService.importArticle(multipartFile, strategyName);
        return Result.ok();
    }
}


