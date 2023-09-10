package com.fly.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_article")
public class Article {
    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章封面
     */
    @JSONField(alternateNames = {"articleCover", "article_cover"})
    private String articleCover;

    /**
     * 文章作者id
     */
    @JSONField(alternateNames = {"userId", "user_id"})
    private Integer userId;

    /**
     * 文章标题
     */
    @JSONField(alternateNames = {"articleTitle", "article_title"})
    private String articleTitle;

    /**
     * 文章内容
     */
    @JSONField(alternateNames = {"articleContent", "article_content"})
    private String articleContent;

    /**
     * 文章分类Id
     */
    @JSONField(alternateNames = {"categoryId", "category_id"})
    private Integer categoryId;

    /**
     * 文章是否置顶
     */
    @JSONField(alternateNames = {"isTop", "is_top"})
    private Integer isTop;

    /**
     * 文章类型（1 原创 2 转载 3 翻译）
     */
    private Integer type;

    /**
     * 原文链接
     */
    @JSONField(alternateNames = {"originalUrl", "original_url"})
    private String originalUrl;

    /**
     * 文章状态（1、公开， 2、私密，3、草稿）
     */
    private Integer status;

    /**
     * 文章是否已经被删除
     */
    @JSONField(alternateNames = {"isDelete", "is_delete"})
    private Integer isDelete;

    /**
     * 发表时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JSONField(alternateNames = {"createTime", "create_time"})
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JSONField(alternateNames = {"updateTime", "update_time"})
    private LocalDateTime updateTime;
}
