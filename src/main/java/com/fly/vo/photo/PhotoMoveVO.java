package com.fly.vo.photo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 移动照片数据
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoMoveVO {

    /**
     * 相册 ID
     */
    @NotNull(message = "相册 ID 不能为空")
    @Schema(description = "相册 ID")
    private Integer albumId;

    /**
     * 照片 ID 列表
     */
    @Schema(description = "照片 ID 列表")
    private List<Integer> photoIdList;

}