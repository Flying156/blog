package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.comment.*;
import com.fly.entity.Comment;
import com.fly.vo.comment.CommentSearchVO;
import com.fly.vo.comment.ViewCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 后台根据条件查询数量
     * @param commentSearchVO 查询条件
     * @return  评论数量
     */
    Long countAdminComments(@Param("commentSearchVO") CommentSearchVO commentSearchVO);

    /**
     * 后台从数据库中查询评论列表
     * @param offset           第几页
     * @param size             页面大小
     * @param commentSearchVO  评论查询条件
     * @return   品论列表
     */
    List<CommentAdminDTO> listAdminComments(@Param("offset") long offset,
                                            @Param("size") long size,
                                            @Param("commentSearchVO")
                                            CommentSearchVO commentSearchVO);


    /**
     * 分页查询前台查看评论所需数据
     */
    List<CommentDTO> listComments(@Param("offset") long offset,
                                  @Param("size") long size,
                                  @Param("viewCommentVO") ViewCommentVO viewCommentVO);

    /**
     * 获取每个父评论的回复数
     * @param commentIdList 父评论 ID
     * @return 回复数
     */
    List<ReplyCountDTO> listReplayCount(List<Integer> commentIdList);

    /**
     * 获取每个父评论的子评论
     * @param commentIdList 父评论 ID
     * @return 子评论列表
     */
    List<ReplyDTO> listReplies(List<Integer> commentIdList);

    List<CommentCountDTO> getTopicIdAndCommentsCount(@Param("talkIdList") List<Integer> talkIdList);
}
