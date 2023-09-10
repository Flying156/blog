package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.dto.comment.CommentAdminDTO;
import com.fly.dto.comment.CommentDTO;
import com.fly.dto.comment.ReplyCountDTO;
import com.fly.dto.comment.ReplyDTO;
import com.fly.entity.Comment;
import com.fly.mapper.CommentMapper;
import com.fly.service.CommentService;
import com.fly.util.*;
import com.fly.vo.comment.CommentSearchVO;
import com.fly.vo.comment.CommentVO;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.comment.ViewCommentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.COMMENT_LIKE_COUNT;
import static com.fly.constant.RedisConst.COMMENT_LIKE_PREFIX;

/**
 * @author Milk
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {


    @Override
    public void saveComment(CommentVO commentVO) {
        // 判断是否开启评论审核
        Integer isCommentReview = ConfigUtils.getCache(WebsiteConfig::getIsCommentReview);
        // 组装评论数据
        Comment comment = Comment.builder()
                .userId(SecurityUtils.getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .topicId(commentVO.getTopicId())
                // 过滤评论额呢绒
                .commentContent(StrRegexUtils.filter(commentVO.getCommentContent()))
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                // 不开启评论审核则评论默认状态为已审核
                .isReview(isCommentReview.equals(TRUE_OF_INT) ? FALSE_OF_INT : TRUE_OF_INT)

                .build();
        save(comment);
        // 邮箱通知
    }

    @Override
    public PageDTO<CommentAdminDTO> listAdminComment(CommentSearchVO commentSearchVO) {
        // 查询符合条件的评论量
        Long adminCommentsCount = baseMapper.countAdminComments(commentSearchVO);
        if (adminCommentsCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        List<CommentAdminDTO> commentList = baseMapper.listAdminComments
                (PageUtils.offset(), PageUtils.size(), commentSearchVO);
        return PageUtils.build(commentList, adminCommentsCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeComments(List<Integer> commentIdList) {
        removeBatchByIds(commentIdList);
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reviewComment(ReviewVO reviewVO) {
        Integer isReview = reviewVO.getIsReview();
        List<Comment> commentList = reviewVO.getIdList()
                .stream()
                .map(commentId -> Comment.builder()
                        .id(commentId)
                        .isReview(isReview)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(commentList);
    }

    @Override
    public void likeComment(Integer commentId) {
        RedisUtils.likeOrUnlike(COMMENT_LIKE_PREFIX,COMMENT_LIKE_COUNT ,  commentId);
    }

    // forEach 可以对对象进行修改并保存，
    @Override
    public PageDTO<CommentDTO> listComments(ViewCommentVO viewCommentVO) {
        // 查询符合条件的评论总量
        Long commentsCount = lambdaQuery()
                .eq(Objects.nonNull(viewCommentVO.getTopicId()),
                        Comment::getTopicId, viewCommentVO.getTopicId())
                .eq(Comment::getType, viewCommentVO.getType())
                .eq(Comment::getIsReview, TRUE_OF_INT)
                .isNull(Comment::getParentId)
                .count();

        if(commentsCount.equals(ZERO_L)){
            return new PageDTO<>();
        }

        // 首先查询一级评论
        List<CommentDTO> commentDTOList = baseMapper.listComments
                (PageUtils.offset(), PageUtils.size(), viewCommentVO);
        // 获取全部评论的点赞
        CompletableFuture<Map<String, Object>> likeFuture
                = AsyncUtils.supplyAsync(() -> RedisUtils.hGetAll(COMMENT_LIKE_COUNT));
        // 获取评论 ID
        List<Integer> commentIdList = commentDTOList
                .stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());
        // 获取每个评论的回复量
        CompletableFuture<Map<Integer, Integer>> replayCountFuture = AsyncUtils.supplyAsync
                (() -> baseMapper.listReplayCount(commentIdList)
                        .stream()
                        .collect(Collectors.toMap(ReplyCountDTO::getCommentId, ReplyCountDTO::getReplyCount)));
        // 获得每个评论的子评论
        List<ReplyDTO> replyList = baseMapper.listReplies(commentIdList);

        // 获取评论点赞数据查询结果
        Map<String, Object> commentIdLikesCountMap
                = AsyncUtils.get(likeFuture, "查询评论点赞数据异常");

        // 封装回复点赞量
        replyList.forEach(replyDTO -> replyDTO.setLikeCount
                ((Integer) commentIdLikesCountMap.get(replyDTO.getId().toString())));

        // 根据评论 ID 映射父评论 ID 和子评论列表
        Map<Integer, List<ReplyDTO>> commentIdRepliesMap = replyList
                .stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));

        // 获取根据评论 ID 查询回复量的结果
        Map<Integer, Integer> commentIdRepliesCountMap
                = AsyncUtils.get(replayCountFuture, "根据评论 ID 查询回复量异常");

        // 封装点赞
        for(CommentDTO comment: commentDTOList){
            Integer commentId = comment.getId();
            // 封装点赞
            comment.setLikeCount((Integer)commentIdLikesCountMap.get(commentId.toString()));
            // 封装回复
            comment.setReplyCount(commentIdRepliesCountMap.get(commentId));
            // 封装回复评论
            comment.setReplyDTOList(commentIdRepliesMap.get(commentId));
        }
        return PageUtils.build(commentDTOList, commentsCount);
    }


}
