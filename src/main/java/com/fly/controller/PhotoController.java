package com.fly.controller;

import com.fly.annotation.OperatingLog;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photo.PhotoAdminDTO;
import com.fly.dto.photo.PhotoDTO;
import com.fly.service.PhotoService;
import com.fly.util.Result;
import com.fly.vo.photo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fly.enums.OperationLogEum.*;

/**
 * @author Milk
 */
@Validated
@Tag(name = "照片模块")
@RestController
public class PhotoController {
    @Resource
    private PhotoService photoService;


    @Operation(summary = "后台查看相册照片或回收站照片")
    @GetMapping("/admin/photos")
    public Result<PageDTO<PhotoAdminDTO>>reviewPhotoManagement
            (@Valid PhotoReviewVO photoReviewVO) {
        return Result.ok(photoService.listAdminPhotos(photoReviewVO));
    }

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传图片")
    @PostMapping("/admin/photos")
    public Result<?> savePhoto(@Valid @RequestBody PhotoVO photoVO){
        photoService.savePhoto(photoVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "回收站删除图片")
    @DeleteMapping("/admin/photos")
    public Result<?> removePhotos(@NotEmpty @RequestBody List<Integer> photoIdList){
        photoService.removePhotos(photoIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新照片删除状态")
    @PutMapping("/admin/photos/delete")
    public Result<?> updatePhotoDelete(@Valid @RequestBody PhotoDeleteVO photoDeleteVO){
        photoService.updatePhotoDelete(photoDeleteVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改照片状态")
    @PutMapping("/admin/photos")
    public Result<?> updatePhoto(@Valid @RequestBody PhotoUpdateVO photoUpdateVO){
        photoService.updatePhoto(photoUpdateVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "转移照片至其他相册")
    @PutMapping("/admin/photos/album")
    public Result<?> updatePhotoAlbum(@Valid @RequestBody PhotoMoveVO photoMoveVO){
        photoService.updatePhotoAlbum(photoMoveVO);
        return Result.ok();
    }

    @Operation(summary = "前台点击查看相册")
    @GetMapping("/albums/{albumId}/photos")
    public Result<PhotoDTO> listPhotosInAlbum(@NotNull @PathVariable Integer albumId){
        return Result.ok(photoService.listPhotosInAlbum(albumId));
    }


}
