package com.fly.dto.photoAlbum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlbumMangeDTO {

    /**
     * 相册 ID
     */
    private Integer id;

    /**
     * 相册名
     */
    private String albumName;

    /**
     * 相册描述
     */
    private String albumDesc;

    /**
     * 相册封面
     */
    private String albumCover;

    /**
     * 照片数量
     */
    private Integer photoCount;

    /**
     * 状态值（1 公开 2 私密）
     */
    private Integer status;

}
