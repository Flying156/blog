package com.fly.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章上下篇
 * @author Milk
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePaginationDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;

}
