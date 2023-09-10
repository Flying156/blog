package com.fly.controller;


import com.fly.annotation.OperatingLog;
import com.fly.dto.blogInfo.AdminBlogInfoDTO;
import com.fly.dto.blogInfo.BlogInfoDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.service.BlogInfoService;
import com.fly.util.Result;
import com.fly.vo.bloginfo.InfoAboutMeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.fly.enums.OperationLogEum.UPDATE;
import static com.fly.enums.OperationLogEum.UPLOAD;

/**
 * @author Milk
 */
@Tag(name = "博客信息模块")
@Validated
@RestController
public class BlogInfoController {

    @Resource
    private BlogInfoService blogInfoService;

    @Operation(summary = "统计访问量信息")
    @PostMapping("/report")
    public Result<?> updateVisitCount(){
        blogInfoService.updateVisitCount();
        return Result.ok();
    }

    @Operation(summary = "浏览博客后台信息")
    @GetMapping("/admin")
    public Result<AdminBlogInfoDTO> browseAdminBlogInfo(){
        return Result.ok(blogInfoService.getAdminBlogInfo());
    }

    @Operation(summary = "浏览博客信息")
    @GetMapping("/")
    public Result<BlogInfoDTO> browseBlogInfo() {
        return Result.ok(blogInfoService.getBlogInfo());
    }


    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新网站配置")
    @PutMapping("/admin/website/config")
    public Result<?> updateWebSiteConfig(@Valid @RequestBody WebsiteConfig websiteConfig){
        blogInfoService.updateWebSiteConfig(websiteConfig);
        return Result.ok();
    }

    @Operation(summary = "获取网站配置")
    @GetMapping("/admin/website/config")
    public Result<WebsiteConfig> getWebSiteConfig(){
        return Result.ok(blogInfoService.getWebSiteConfig());
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新关于我")
    @PutMapping("/admin/about")
    public Result<?> updateAboutMe(@Valid @RequestBody InfoAboutMeVO infoAboutMeVO){
        blogInfoService.updateAboutMe(infoAboutMeVO);
        return Result.ok();
    }

    @Operation(summary = "获取关于我")
    @GetMapping("/about")
    public Result<String> getAboutMe(){
        return Result.ok(blogInfoService.getAboutMe());
    }

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传网站图片")
    @PostMapping("/admin/config/images")
    public Result<String> uploadWebsiteImage(@NotNull @RequestParam("file") MultipartFile file){
        return Result.ok(blogInfoService.uploadWebsiteImage(file));
    }
}
