package com.fly.dto.tag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签管理类
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagMangeDTO {
    /**
     * 标签 ID
     */
    private Integer id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 文章量
     */
    private Integer articleCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
