package com.fly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.photo.PhotoAdminDTO;
import com.fly.dto.photo.PhotoDTO;
import com.fly.entity.Photo;
import com.fly.entity.PhotoAlbum;
import com.fly.enums.AlbumStatusEnum;
import com.fly.mapper.PhotoMapper;
import com.fly.service.PhotoAlbumService;
import com.fly.service.PhotoService;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.util.PageUtils;
import com.fly.util.SecurityUtils;
import com.fly.vo.photo.*;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fly.constant.CacheConst.PHOTO_ALBUM;
import static com.fly.constant.GenericConst.FALSE_OF_INT;

/**
 * @author Milk
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Resource
    private PhotoAlbumService photoAlbumService;

    @Override
    public PageDTO<PhotoAdminDTO> listAdminPhotos(PhotoReviewVO photoReviewVO) {
        IPage<Photo> page = lambdaQuery()
                .eq(Objects.nonNull(photoReviewVO.getAlbumId()), Photo::getAlbumId, photoReviewVO.getAlbumId())
                .eq(Photo::getIsDelete, photoReviewVO.getIsDelete())
                .orderByDesc(Photo::getId)
                .orderByDesc(Photo::getCreateTime)
                .page(PageUtils.getPage());
        List<PhotoAdminDTO> photoAdminDTOList = ConvertUtils.convertList(page.getRecords(), PhotoAdminDTO.class);
        return PageUtils.build(photoAdminDTOList, page.getTotal());
    }

    @Override
    public void savePhoto(PhotoVO photoVO) {
        List<String> photoUrlList = photoVO.getPhotoUrlList();

        photoUrlList.forEach(photoUrl -> {
            Photo photo = Photo.builder()
                    .photoSrc(photoUrl)
                    .albumId(photoVO.getAlbumId())
                    .photoName(SecurityUtils.getUniqueName())
                    .build();
            baseMapper.insert(photo);
        });
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void removePhotos(List<Integer> photoIdList) {
        removeBatchByIds(photoIdList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void updatePhotoDelete(PhotoDeleteVO photoDeleteVO) {
        // 恢复或删除图片
        Integer isDelete = photoDeleteVO.getIsDelete();
        List<Integer> photoIdList = photoDeleteVO.getIdList();
        List<Photo> photoList = photoIdList
                .stream()
                .map(photoId -> Photo.builder()
                        .id(photoId)
                        .isDelete(isDelete)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(photoList);
        // 如果对应的操作是恢复
        if(isDelete.equals(FALSE_OF_INT)){
            // 如果相册被删除需要恢复
            List<PhotoAlbum> photoAlbumList = lambdaQuery()
                    .select(Photo::getAlbumId)
                    .in(Photo::getId, photoIdList)
                    // GROUP BY 可以去重
                    .groupBy(Photo::getAlbumId)
                    .list()
                    .stream()
                    .map(Photo::getAlbumId)
                    .map(albumId -> PhotoAlbum.builder()
                            .id(albumId)
                            .isDelete(FALSE_OF_INT)
                            .build())
                    .collect(Collectors.toList());
            photoAlbumService.updateBatchById(photoAlbumList);

        }
    }

    @Override
    public void updatePhoto(PhotoUpdateVO photoUpdateVO) {
        updateById(BeanCopyUtils.copy(photoUpdateVO, Photo.class));
    }

    @Override
    public void updatePhotoAlbum(PhotoMoveVO photoMoveVO) {
        List<Integer> photoIdList = photoMoveVO.getPhotoIdList();
        lambdaUpdate().set(Photo::getAlbumId, photoMoveVO.getAlbumId())
                .in(Photo::getId, photoIdList).update();
    }

    @Override
    public PhotoDTO listPhotosInAlbum(Integer albumId) {
        // 查询相册信息
        PhotoAlbum photoAlbum = Optional
                .ofNullable(photoAlbumService.lambdaQuery()
                        .select(PhotoAlbum::getAlbumCover, PhotoAlbum::getAlbumName)
                        .eq(PhotoAlbum::getId, albumId)
                        .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                        .eq(PhotoAlbum::getStatus, AlbumStatusEnum.PUBLIC.getStatus())
                        .one())
                .orElseThrow(() -> new ServiceException("相册不存在"));
        // 查询照片信息
        List<String>photoUrlList = lambdaQuery()
                .select(Photo::getPhotoSrc)
                .eq(Photo::getAlbumId, albumId)
                .eq(Photo::getIsDelete, FALSE_OF_INT)
                .orderByDesc(Photo::getId)
                .page(PageUtils.getPage())
                .getRecords()
                .stream()
                .map(Photo::getPhotoSrc)
                .collect(Collectors.toList());
        return PhotoDTO.builder()
                .photoAlbumCover(photoAlbum.getAlbumCover())
                .photoAlbumName(photoAlbum.getAlbumName())
                .photoList(photoUrlList)
                .build();
    }
}
