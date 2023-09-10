package com.fly.dto.chatRecord;

import com.fly.entity.ChatRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天记录数据
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordDTO {

    /**
     * 历史记录
     */
    private List<ChatRecord> chatRecordList;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * IP 来源
     */
    private String ipSource;

}
