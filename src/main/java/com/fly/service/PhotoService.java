package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photo.PhotoAdminDTO;
import com.fly.dto.photo.PhotoDTO;
import com.fly.entity.Photo;
import com.fly.vo.photo.*;

import java.util.List;

/**
 * @author Milk
 */
public interface PhotoService extends IService<Photo> {
    /**
     * 获取后台查看相册照片或回收站照片的分页数据
     */
    PageDTO<PhotoAdminDTO> listAdminPhotos(PhotoReviewVO photoReviewVO);

    /**
     * 保存照片
     * @param photoVO 照片数据
     */
    void savePhoto(PhotoVO photoVO);

    /**
     * 批量删除
     * @param photoIdList 照片 ID
     */
    void removePhotos(List<Integer> photoIdList);

    /**
     * 更新照片删除状态
     * @param photoDeleteVO 照片 ID，删除状态
     */
    void updatePhotoDelete(PhotoDeleteVO photoDeleteVO);

    /**
     * 更新照片
     * @param photoUpdateVO 需要修改的属性
     */
    void updatePhoto(PhotoUpdateVO photoUpdateVO);

    /**
     * 更改照片相册位置
     * @param photoMoveVO 属性
     */
    void updatePhotoAlbum(PhotoMoveVO photoMoveVO);

    /**
     * 查看相册内照片
     * @param albumId 相册 ID
     * @return 照片列表
     */
    PhotoDTO listPhotosInAlbum(Integer albumId);
}
