package com.fly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.dto.message.MessageAdminDTO;
import com.fly.dto.message.MessageDTO;
import com.fly.entity.Message;
import com.fly.mapper.MessageMapper;
import com.fly.service.MessageService;
import com.fly.util.*;
import com.fly.vo.comment.ReviewVO;
import com.fly.vo.message.MessageSearchVO;
import com.fly.vo.message.MessageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.fly.constant.GenericConst.FALSE_OF_INT;
import static com.fly.constant.GenericConst.TRUE_OF_INT;

/**
 * @author Milk
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public PageDTO<MessageAdminDTO> listMessageAdmin(MessageSearchVO messageSearchVO) {
        String keywords = messageSearchVO.getKeywords();
        Integer isReview = messageSearchVO.getIsReview();

        IPage<Message> page = lambdaQuery()
                .eq(Objects.nonNull(isReview), Message::getIsReview, isReview)
                .like(StrRegexUtils.isNotBlank(keywords), Message::getNickname, keywords)
                .orderByDesc(Message::getId)
                .page(PageUtils.getPage());

        return PageUtils.build(ConvertUtils.convertList(page.getRecords(), MessageAdminDTO.class), page.getTotal());

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeMessages(List<Integer> messageIdList) {
        removeBatchByIds(messageIdList);
    }

    @Override
    public void reviewMessages(ReviewVO reviewVO) {
        lambdaUpdate()
                .set(Message::getIsReview, reviewVO.getIsReview())
                .in(Message::getId, reviewVO.getIdList())
                .update();

    }

    @Override
    public List<MessageDTO> listMessage() {
        List<Message> messageList = lambdaQuery()
                .select(Message::getId, Message::getNickname, Message::getAvatar, Message::getMessageContent, Message::getTime)
                .list();
        return ConvertUtils.convertList(messageList, MessageDTO.class);
    }

    @Override
    public void saveMessage(MessageVO messageVO) {
        Message message = BeanCopyUtils.copy(messageVO, Message.class);
        // 判断是否需要审核
        Integer isMessageReview = ConfigUtils.getCache(WebsiteConfig::getIsMessageReview);
        message.setIsReview(isMessageReview.equals(TRUE_OF_INT) ?
                FALSE_OF_INT : TRUE_OF_INT
        );
        // 获取用户或游客的 IP
        String ipAddress = WebUtils.getCurrentIpAddress();

        String ipSource = WebUtils.getCurrentIpSource(ipAddress);
        message.setIpAddress(ipAddress);
        message.setIpSource(ipSource);
        // 内容过滤
        message.setMessageContent(StrRegexUtils.filter(message.getMessageContent()));
        save(message);

    }
}
