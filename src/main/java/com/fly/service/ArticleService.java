package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.*;
import com.fly.entity.Article;
import com.fly.vo.article.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Milk
 */
public interface ArticleService extends IService<Article> {

    /**
     * 根据条件后台分页查询文章
     * @param articleSearchVO 查询封装类
     * @return 查询结果
     */
    PageDTO<ArticleMangeDTO> listAdminArticles(ArticleSearchVO articleSearchVO);

    /**
     * 物理删除文章，将其从数据库中删除
     * @param articleIdList ID列表
     */
    void removeArticlePhysically(List<Integer> articleIdList);

    /**
     * 逻辑删除或恢复文章，修改 isDelete 字段
     * @param tableLogicVO 通用实体类
     */
    void recoverOrRemoveArticleLogically(TableLogicVO tableLogicVO);

    /**
     * 修改指定文章的指定状态
     * @param articleTopVO 实体类
     */
    void updateArticleTop(ArticleTopVO articleTopVO);

    /**
     * 根据 ID 查询文章信息
     * @param articleId 文章 ID
     * @return 文章信息
     */
    ArticleVO getAdminArticle(Integer articleId);

    /**
     * 修改或发布文章
     * @param articleVO 需要修改的内容
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 博客首页列出文章
     * @return 文章列表
     */
    List<HomePageArticleDTO> listHomePageArticles();

    /**
     * 上传图片
     * @param multipartFile 图片
     * @return 图片外链
     */
    String saveArticleCover(MultipartFile multipartFile);

    /**
     * 前台根据分类ID查看分类或标签文章预览
     * @param articlePreviewVO 查询条件
     * @return  文章
     */
    ArticlePreviewDTO getArticlePreview(ArticlePreviewVO articlePreviewVO);

    /**
     * 查看文章内容及推荐文章
     * @param articleId 文章ID
     * @return 文章相关内容
     */
    ArticleDTO getArticle(Integer articleId);

    /**
     * 前台查看文章归档(依据时间线梳理发布的文章)
     * @return 列表
     */
    PageDTO<ArticleArchiveDTO> getArticleArchives();

    /**
     * 给文章点赞
     * @param articleId 文章 ID
     */
    void likeArticle(Integer articleId);

    /**
     * 根据关键词查询文章
     */
    List<ArticleSearchDTO> listArticlesBySearch(String keywords);

    /**
     * 导出文章
     * @param articleIdList 文章列表
     */
    List<String> exportArticles(List<Integer> articleIdList);

    /**
     * 导入文章
     * @param multipartFile 文件
     * @param strategyName 名称
     */
    void importArticle(MultipartFile multipartFile, String strategyName);
}
