package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.message.MessageAdminDTO;
import com.fly.dto.message.MessageDTO;
import com.fly.entity.Message;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.message.MessageSearchVO;
import com.fly.vo.message.MessageVO;

import java.util.List;

/**
 * @author Milk
 */
public interface MessageService extends IService<Message> {

    /**
     * 后台留言列表
     * @param messageSearchVO 留言搜素条件
     * @return      留言列表
     */
    PageDTO<MessageAdminDTO> listMessageAdmin(MessageSearchVO messageSearchVO);

    /**
     * 批量删除留言
     * @param messageIdList 留言列表 ID
     */
    void removeMessages(List<Integer> messageIdList);

    /**
     * 批量审核留言
     * @param reviewVO 留言列表
     */
    void reviewMessages(ReviewVO reviewVO);

    /**
     * 前台查看留言
     * @return 留言列表
     */
    List<MessageDTO> listMessage();

    /**
     * 发送留言
     */
    void saveMessage(MessageVO messageVO);
}
