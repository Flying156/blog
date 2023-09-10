package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.chatRecord.ChatMessageDTO;
import com.fly.dto.chatRecord.ChatRecordDTO;
import com.fly.entity.ChatRecord;
import com.fly.enums.ChatEnum;
import com.fly.handler.WebSocketHandler;
import com.fly.mapper.ChatRecordMapper;
import com.fly.service.ChatRecordService;
import com.fly.util.TimeUtils;
import com.fly.util.WebUtils;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.fly.constant.GenericConst.TWELVE;

/**
 * @author Milk
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    @Override
    public void updateOnlineUserCount(int size) {
        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .type(ChatEnum.ONLINE_COUNT.getType())
                .data(size)
                .build();
        WebSocketHandler.broadcastMessage(chatMessageDTO);
    }

    @Override
    public ChatRecordDTO getHistoryMessage(String ipAddress) {
        // 获取 12 小时内的聊天记录
        List<ChatRecord> chatRecordList = lambdaQuery()
                .ge(ChatRecord::getCreateTime,
                        TimeUtils.offset(TimeUtils.now(), -TWELVE, ChronoUnit.HOURS)).list();
        return ChatRecordDTO.builder()
                .chatRecordList(chatRecordList)
                .ipAddress(ipAddress)
                .ipSource(WebUtils.getIpSource(ipAddress))
                .build();
    }
}
