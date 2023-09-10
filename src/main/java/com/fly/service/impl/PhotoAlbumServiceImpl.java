package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photoAlbum.AlbumMangeDTO;
import com.fly.dto.photoAlbum.PhotoAlbumDTO;
import com.fly.entity.Photo;
import com.fly.entity.PhotoAlbum;
import com.fly.enums.AlbumStatusEnum;
import com.fly.mapper.PhotoAlbumMapper;
import com.fly.mapper.PhotoMapper;
import com.fly.service.PhotoAlbumService;
import com.fly.util.*;
import com.fly.vo.photoAlbum.PhotoAlbumVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static com.fly.constant.CacheConst.PHOTO_ALBUM;
import static com.fly.constant.GenericConst.*;

/**
 * @author Milk
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements PhotoAlbumService {

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private QiNiuUtils qiNiuUtils;

    @Override
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public String savePhotoAlbumCover(MultipartFile multipartFile) {
        return qiNiuUtils.uploadImage(multipartFile);

    }

    @Override
    public PageDTO<AlbumMangeDTO> listManageAlbums(String keywords) {
        Long count = lambdaQuery()
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .like(StrRegexUtils.isNotBlank(keywords), PhotoAlbum::getAlbumName, keywords)
                .count();
        if(count.equals(ZERO_L)){
            return new PageDTO<>();
        }
        List<AlbumMangeDTO> albumMangeDTOList = baseMapper.listAdminPhotoAlbums(PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(albumMangeDTOList, count);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void saveOrUpdateAlbum(PhotoAlbumVO photoAlbumVO) {
        boolean save = photoAlbumVO.getId() == null;
        // 判断是否是新增相册
        if(save){
            boolean exists = lambdaQuery()
                    .eq(PhotoAlbum::getAlbumName, photoAlbumVO.getAlbumName())
                    .exists();
            if (exists) {
                throw new ServiceException("相册名已存在");
            }
        }
        PhotoAlbum photoAlbum = BeanCopyUtils.copy(photoAlbumVO, PhotoAlbum.class);
        saveOrUpdate(photoAlbum);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void removeAlbum(Integer albumId) {
        // 首先判断相册是否有照片
        boolean exists = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(Photo::getAlbumId, albumId)
                .exists();
        if (!exists) {
            removeById(albumId);
        }
        // 存在全部删除
        lambdaUpdate()
                .set(PhotoAlbum::getIsDelete, TRUE_OF_INT)
                .eq(PhotoAlbum::getId, albumId)
                .update();
        new LambdaUpdateChainWrapper<>(photoMapper)
                .set(Photo::getIsDelete, TRUE_OF_INT)
                .eq(Photo::getAlbumId, albumId)
                .update();
    }

    @Override
    @Cacheable(cacheNames = PHOTO_ALBUM, key = "#root.methodName", sync = true)
    public List<PhotoAlbumDTO> listAdminPhotoAlbums() {
        List<PhotoAlbum> photoAlbumList = lambdaQuery()
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .list();
        return ConvertUtils.convertList(photoAlbumList, PhotoAlbumDTO.class);
    }

    @Override
    public AlbumMangeDTO getManageAlbum(Integer albumId) {
        PhotoAlbum photoAlbum = baseMapper.selectById(albumId);
        AlbumMangeDTO albumMangeDTO = BeanCopyUtils.copy(photoAlbum, AlbumMangeDTO.class);
        Long photosCount = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(Photo::getIsDelete, FALSE_OF_INT)
                .eq(Photo::getAlbumId, albumId)
                .count();
        albumMangeDTO.setPhotoCount(photosCount.intValue());
        return albumMangeDTO;
    }


    @Override
    @Cacheable(cacheNames = PHOTO_ALBUM, key = "#root.methodName", sync = true)
    public List<PhotoAlbumDTO> listPhotoAlbums() {
        List<PhotoAlbum> albumList = lambdaQuery()
                .select(PhotoAlbum::getId, PhotoAlbum::getAlbumName, PhotoAlbum::getAlbumCover, PhotoAlbum::getAlbumDesc)
                .eq(PhotoAlbum::getStatus, AlbumStatusEnum.PUBLIC.getStatus())
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .orderByDesc(PhotoAlbum::getId)
                .list();
        return ConvertUtils.convertList(albumList, PhotoAlbumDTO.class);
    }
}
