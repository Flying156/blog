package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.article.*;
import com.fly.entity.Article;
import com.fly.vo.article.ArticlePreviewVO;
import com.fly.vo.article.ArticleSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 查询每日发布文章的数量
     * @return DailyArticleDTO
     */
    List<DailyArticleDTO> listDailyArticles();

    /**
     * 根据条件查询文章数量
     * @param articleSearchVO 搜素条件
     * @return 文章数量
     */
    Long articleCount(@Param("articleSearchVO") ArticleSearchVO articleSearchVO);

    List<ArticleMangeDTO>listAdminArticles(@Param("offset") long offset, @Param("size") long size, @Param("articleSearchVO") ArticleSearchVO articleSearchVO);

    /**
     * 前台查询文章
     * @param offset 页面第几页
     * @param size 页面大小
     * @return 文章列表
     */
    List<HomePageArticleDTO> listHomePageArticles(long offset, long size);

    /**
     * 首页查询
     * @param offset 页面第几页
     * @param size   页面大小
     * @param articlePreviewVO 搜索条件
     * @return  文章列表
     */
    List<PreviewDTO> listPreviewArticle(@Param("offset") long offset,  @Param("size") long size, @Param("articlePreviewVO")  ArticlePreviewVO articlePreviewVO);

    /**
     * 根据 ID 查询文章的有关信息
     * @param articleId 文章 ID
     * @return 文章信息
     */
    ArticleDTO getArticle(Integer articleId);

    /**
     * 获取 6 篇推荐文章
     * @param articleId 当前文章 ID
     * @return 推荐文章列表
     */
    List<ArticleRecommendDTO> listArticleRecommendArticles(Integer articleId);
}
