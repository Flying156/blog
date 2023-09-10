package com.fly.dto.blogInfo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fly.dto.article.ArticleRankDTO;
import com.fly.dto.article.DailyArticleDTO;
import com.fly.dto.category.CategoryDTO;
import com.fly.dto.tag.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 博客首页后台统计信息
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminBlogInfoDTO {

    /**
     * 访问量
     */
    private Integer viewsCount;

    /**
     * 留言量
     */
    private Long messageCount;

    /**
     * 用户量
     */
    private Long userCount;

    /**
     * 文章量
     */
    private Long articleCount;

    /**
     * 分类统计
     */
    private List<CategoryDTO> categoryDTOList;


    /**
     * 标签列表
     */
    private List<TagDTO> tagDTOList;

    /**
     * 文章统计列表
     */
    private List<DailyArticleDTO> articleStatisticsList;

    /**
     * 一周用户量集合
     */
    @JSONField(alternateNames = {"dailyVisitDTOList", "uniqueViewDTOList"})
    private List<DailyVisitDTO> dailyVisitDTOList;

    /**
     * 文章浏览量排行
     */
    private List<ArticleRankDTO> articleRankDTOList;
}
