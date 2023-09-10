package com.fly.service.impl;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.tag.TagDTO;
import com.fly.dto.tag.TagMangeDTO;
import com.fly.entity.ArticleTag;
import com.fly.entity.Tag;
import com.fly.mapper.ArticleTagMapper;
import com.fly.mapper.TagMapper;
import com.fly.service.TagService;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.util.PageUtils;
import com.fly.util.StrRegexUtils;
import com.fly.vo.tag.TagVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.fly.constant.CacheConst.TAG;
import static com.fly.constant.GenericConst.ZERO_L;

/**
 * @author Milk
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService{

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = TAG, allEntries = true)
    public void removeTags(List<Integer> tagIdList) {

        // 首先判断标签下是否有文章
        boolean exists = new LambdaQueryChainWrapper<>(articleTagMapper)
                .in(ArticleTag::getId, tagIdList)
                .exists();
        if(exists){
            throw new ServiceException("删除失败，该标签下存在文章");
        }
        removeBatchByIds(tagIdList);
    }

    @Override
    @Cacheable(cacheNames = TAG, key = "#root.methodName", sync = true,
        condition = "T(com.fly.util.StrRegexUtils).isBlank(#keywords)")
    public PageDTO<TagMangeDTO> browseTags(String keywords) {
        // 标签数量
        Long tagCount = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords), Tag::getTagName, keywords)
                .count();
        if(tagCount.equals(ZERO_L)){
            return new PageDTO<>();
        }

        List<TagMangeDTO> adminTagList = baseMapper
                .listAdminTags(PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(adminTagList, tagCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = TAG, allEntries = true)
    public void saveOrUpdateTag(TagVO tagVO) {
        boolean save = tagVO.getId() == null;
        if(save) {
            boolean exists = lambdaQuery()
                    .eq(Tag::getTagName, tagVO.getTagName())
                    .exists();
            if (exists) {
                throw new ServiceException("标签名已存在");
            }
        }
        saveOrUpdate(BeanCopyUtils.copy(tagVO, Tag.class));
    }

    @Override
    @Cacheable(cacheNames = TAG, key = "#root.methodName", sync = true)
    public List<TagDTO> listAdminArticleTags() {
        List<Tag> tagList = lambdaQuery()
                .select(Tag::getId, Tag::getTagName)
                .orderByDesc(Tag::getId).list();
        return ConvertUtils.convertList(tagList, TagDTO.class);
    }

    @Override
    public List<String> listArticleTagNames(Integer articleId) {
        return baseMapper.listArticleTagNames(articleId);
    }

    @Override
    @Cacheable(cacheNames = TAG, key = "#root.methodName", sync = true)
    public PageDTO<TagDTO> listTag() {
        Long tagCount = lambdaQuery().count();
        if(tagCount.equals(ZERO_L)){
            return new PageDTO<>();
        }
        List<Tag> tagList = lambdaQuery().list();
        List<TagDTO> tagDTOList = ConvertUtils.convertList(tagList, TagDTO.class);
        return PageUtils.build(tagDTOList, tagCount);
    }
}
