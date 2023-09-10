package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photoAlbum.AlbumMangeDTO;
import com.fly.dto.photoAlbum.PhotoAlbumDTO;
import com.fly.service.PhotoAlbumService;
import com.fly.util.Result;
import com.fly.vo.photoAlbum.PhotoAlbumVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.*;

/**
 * @author Milk
 */
@Tag(name = "相册模块")
@Validated
@RestController
public class PhotoAlbumController {

    @Resource
    private PhotoAlbumService photoAlbumService;

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "保存相册封面")
    @PostMapping("/admin/photos/albums/cover")
    public Result<String> savePhotoAlbumCover(@NotNull @RequestParam("file")MultipartFile multipartFile) {
        return Result.ok(photoAlbumService.savePhotoAlbumCover(multipartFile));
    }

    @Operation(summary = "查看后台相册管理")
    @GetMapping("/admin/photos/albums")
    public Result<PageDTO<AlbumMangeDTO>> reviewAlbumManagement(
            @RequestParam(required = false) String keywords) {
        return Result.ok(photoAlbumService.listManageAlbums(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改相册")
    @PostMapping("/admin/photos/albums")
    public Result<?> saveOrUpdatePhotoAlbum(@Valid @RequestBody PhotoAlbumVO photoAlbumVO){
        photoAlbumService.saveOrUpdateAlbum(photoAlbumVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除相册")
    @DeleteMapping("/admin/photos/albums/{albumId}")
    public Result<?> removeAlbum(@NotNull @PathVariable Integer albumId){
        photoAlbumService.removeAlbum(albumId);
        return Result.ok();
    }

    @Operation(summary = "后台点击相册")
    @GetMapping("/admin/photos/albums/info")
    public Result<List<PhotoAlbumDTO>> whenAdminClicksAlbum(){
        return Result.ok(photoAlbumService.listAdminPhotoAlbums());
    }

    @Operation(summary = "根据ID查询相册的详细信息")
    @GetMapping("/admin/photos/albums/{albumId}/info")
    public Result<AlbumMangeDTO> whenAdminClicksAlbum(@NotNull @PathVariable Integer albumId) {
        return Result.ok(photoAlbumService.getManageAlbum(albumId));
    }

    @Operation(summary = "前台查看相册")
    @GetMapping("/photos/albums")
    public Result<List<PhotoAlbumDTO>> viewAlbums(){
        return Result.ok(photoAlbumService.listPhotoAlbums());
    }

}
