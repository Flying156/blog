package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.chatRecord.ChatRecordDTO;
import com.fly.entity.ChatRecord;

/**
 * @author Milk
 */
public interface ChatRecordService extends IService<ChatRecord> {

    /**
     * 更新在线聊天室在线人数
     * @param size 在线人数
     */
    void updateOnlineUserCount(int size);

    /**
     * 获取历史消息
     * @param ipAddress IP 地址
     * @return  历史消息
     */
    ChatRecordDTO getHistoryMessage(String ipAddress);
}
