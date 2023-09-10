package com.fly.dto.talk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TalkMangeDTO {
    /**
     * 说说 ID
     */
    private Integer id;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 图片 URL（JSON 数组）
     */
    private String images;

    /**
     * 是否置顶（0 否 1 是）
     */
    private Integer isTop;

    /**
     * 状态（1 公开 2 私密）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
