package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.comment.CommentAdminDTO;
import com.fly.dto.comment.CommentDTO;
import com.fly.entity.Comment;
import com.fly.vo.comment.CommentSearchVO;
import com.fly.vo.comment.CommentVO;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.comment.ViewCommentVO;

import java.util.List;

/**
 * @author Milk
 */
public interface CommentService extends IService<Comment> {

    /**
     * 后台查询评论列表
     * @param commentSearchVO 搜素条件
     * @return  评论列表
     */
    PageDTO<CommentAdminDTO> listAdminComment(CommentSearchVO commentSearchVO);

    /**
     * 批量删除评论
     * @param commentIdList 评论 ID
     */
    void removeComments(List<Integer> commentIdList);

    /**
     * 前台查看评论
     * @param viewCommentVO 前台的不同场合的条件
     * @return 评论列表
     */
    PageDTO<CommentDTO> listComments(ViewCommentVO viewCommentVO);

    /**
     * 发表评论
     * @param commentVO 评论
     */
    void saveComment(CommentVO commentVO);

    /**
     * 审核评论
     * @param reviewVO 评论
     */
    void reviewComment(ReviewVO reviewVO);

    /**
     * 点赞评论
     * @param commentId 评论 ID
     */
    void likeComment(Integer commentId);
}
