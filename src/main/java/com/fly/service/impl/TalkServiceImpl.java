package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.comment.CommentCountDTO;
import com.fly.dto.talk.TalkDTO;
import com.fly.dto.talk.TalkMangeDTO;
import com.fly.entity.Talk;
import com.fly.enums.TalkStatusEnum;
import com.fly.mapper.CommentMapper;
import com.fly.mapper.TalkMapper;
import com.fly.service.TalkService;
import com.fly.util.*;
import com.fly.vo.talk.TalkVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.CacheConst.TALK;
import static com.fly.constant.DatabaseConst.LIMIT_10;
import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.TALK_LIKE_COUNT;

/**
 * @author Milk
 */
@Service
public class TalkServiceImpl extends ServiceImpl<TalkMapper, Talk> implements TalkService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public PageDTO<TalkMangeDTO> listAdminTalks(Integer status) {

        Long adminTalkCount = lambdaQuery().eq(
                Objects.nonNull(status), Talk::getStatus, status)
                .count();
        if(adminTalkCount.equals(ZERO_L)){
            return new PageDTO<>();
        }

        List<TalkMangeDTO> talkMangeDTOList = baseMapper.
                listAdminTalks(PageUtils.offset(), PageUtils.size(), status);
        return PageUtils.build(talkMangeDTOList, adminTalkCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = TALK, allEntries = true)
    public void saveOrUpdateTalk(TalkVO talkVO) {
        Talk talk = BeanCopyUtils.copy(talkVO, Talk.class);
        talk.setUserId(SecurityUtils.getUserInfoId());
        saveOrUpdate(talk);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = TALK, allEntries = true)
    public void removeTalks(List<Integer> talkIdList) {
        removeBatchByIds(talkIdList);
    }

    @Override
    public TalkMangeDTO getAdminTalk(Integer talkId) {
        Talk talk = baseMapper.selectById(talkId);
        return BeanCopyUtils.copy(talk, TalkMangeDTO.class);
    }

    @Override
    @Cacheable(cacheNames = TALK, key = "#root.methodName", sync = true)
    public PageDTO<TalkDTO> listTalks() {
        // 查询公开的说说总量
        Long talkCount = lambdaQuery()
                .eq(Talk::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .count();
        if (talkCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 获取说说点赞量
        CompletableFuture<Map<String, Object>> future = AsyncUtils.supplyAsync
                (() -> RedisUtils.hGetAll(TALK_LIKE_COUNT));
        // 查询分页的说说数据
        List<TalkDTO> talkList = baseMapper.listTalks(PageUtils.offset(), PageUtils.size());

        // 获取评论评论 ID 和点赞量的映射
        Map<String, Object> topicIdLikesCountMap
                = AsyncUtils.get(future, "查询说说点赞量异常");

        // 查询说说 ID
        List<Integer> talkIdList = talkList.stream()
                .map(TalkDTO::getId).collect(Collectors.toList());

        // 查询说说和评论数量的映射

        Map<Integer, Integer> topicIdCommentsCountMap
             = commentMapper.getTopicIdAndCommentsCount(talkIdList)
                .stream()
                .collect(Collectors.toMap(CommentCountDTO::getTopicId, CommentCountDTO::getCommentCount));

        for(TalkDTO talk : talkList){
            Integer topicId = talk.getId();
            talk.setCommentCount(topicIdCommentsCountMap.get(topicId));
            talk.setLikeCount((Integer) topicIdLikesCountMap.get(topicId.toString()));
        }

        return PageUtils.build(talkList, talkCount);
    }

    @Override
    @Cacheable(cacheNames = TALK, key = "#root.methodName", sync = true)
    public List<String> listHomePageTalks() {
        return lambdaQuery()
                .select(Talk::getContent)
                .eq(Talk::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .orderByDesc(Talk::getIsTop)
                .orderByDesc(Talk::getId)
                .last(LIMIT_10)
                .list()
                .stream()
                .map(Talk::getContent)
                // 处理说说内容（删除标签，保留最多 200 字符）
                .map(content -> StrRegexUtils.deleteTag(content.length() > TWO_HUNDRED ?
                        content.substring(ZERO, TWO_HUNDRED) : content))
                .collect(Collectors.toList());
    }
}
