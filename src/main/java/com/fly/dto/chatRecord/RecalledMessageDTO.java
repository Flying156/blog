package com.fly.dto.chatRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Milk
 * @Date 2023/9/5 17:13
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecalledMessageDTO {

    /**
     * 消息 ID
     */
    private Integer id;

    /**
     * 是否为语音
     */
    private Boolean isVoice;

}