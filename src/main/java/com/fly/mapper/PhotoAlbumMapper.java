package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.photoAlbum.AlbumMangeDTO;
import com.fly.entity.PhotoAlbum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {
    /**
     * 分页查询
     * @param offset 页数
     * @param size 页面大小
     * @param keywords 相册数量
     * @return 相册列表
     */
    List<AlbumMangeDTO> listAdminPhotoAlbums(long offset, long size, String keywords);
}
