package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.tag.TagDTO;
import com.fly.dto.tag.TagMangeDTO;
import com.fly.entity.Tag;
import com.fly.vo.tag.TagVO;

import java.util.List;

/**
 * @author Milk
 */
public interface TagService extends IService<Tag> {
    /**
     * 删除标签
     * @param tagIdList 标签列表
     */
    void removeTags(List<Integer> tagIdList);

    /**
     * 管理端后台标签
     * @param keywords 标签名称
     * @return 标签列表
     */
    PageDTO<TagMangeDTO> browseTags(String keywords);

    /**
     * 修改或新增标签
     * @param tagVO  需要修改的数据
     */
    void saveOrUpdateTag(TagVO tagVO);

    /**
     * 后台文章列表获取文章标签
     * @return 标签列表
     */
    List<TagDTO> listAdminArticleTags();

    /**
     * 获取文章的所有标签名
     */
    List<String> listArticleTagNames(Integer articleId);

    /**
     * 前台获取标签列表
     * @return 标签列表
     */
    PageDTO<TagDTO> listTag();
}
