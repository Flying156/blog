package com.fly.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章访问量排行
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRankDTO {
    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 浏览量
     */
    private Integer viewsCount;

}
