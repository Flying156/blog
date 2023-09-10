package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photoAlbum.AlbumMangeDTO;
import com.fly.dto.photoAlbum.PhotoAlbumDTO;
import com.fly.entity.PhotoAlbum;
import com.fly.vo.photoAlbum.PhotoAlbumVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Milk
 */
public interface PhotoAlbumService extends IService<PhotoAlbum> {
    /**
     * 管理端查看相册列表
     * @param keywords 搜索框中相册名称
     * @return 献策列表
     */
    PageDTO<AlbumMangeDTO> listManageAlbums(String keywords);

    /**
     * 上传相册封面
     * @param multipartFile 文件
     */
    String savePhotoAlbumCover(MultipartFile multipartFile);

    /**
     * 保存或修改相册
     * @param photoAlbumVO 数据
     */
    void saveOrUpdateAlbum(PhotoAlbumVO photoAlbumVO);

    /**
     * 删除相册
     * @param albumId 相册 ID
     */
    void removeAlbum(Integer albumId);

    /**
     * 详细查看相册信息
     * @return 信息
     */
    List<PhotoAlbumDTO> listAdminPhotoAlbums();

    /**
     * 根据 ID 查看相册具体信息
     * @param albumId  相册 ID
     * @return 相册信息
     */
    AlbumMangeDTO getManageAlbum(Integer albumId);

    /**
     * 前台查看相册
     * @return 相册列表
     */
    List<PhotoAlbumDTO> listPhotoAlbums();
}
