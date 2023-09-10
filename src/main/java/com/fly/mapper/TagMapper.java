package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.tag.TagMangeDTO;
import com.fly.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    List<TagMangeDTO> listAdminTags(@Param("offset") Long offset,
                                    @Param("size") Long size,
                                    @Param("keywords") String keywords);
    /**
     * 根据文章 ID 查询标签名
     */
    List<String> listArticleTagNames(Integer articleId);
}
