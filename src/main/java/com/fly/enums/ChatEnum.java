package com.fly.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 聊天信息枚举
 *
 * @author Milk
 */

@Getter
@AllArgsConstructor
public enum ChatEnum {

    /**
     *  在线人数
     */
    ONLINE_COUNT(2, "在线用户"),

    /**
     * 历史消息
     */
    HISTORY_MESSAGE(1, "历史消息"),


    /**
     * 发送消息
     */
    SEND_MESSAGE(3, "消息"),


    /**
     * 撤回消息
     */
    RECALL_MESSAGE(4, "撤回消息"),

    /**
     * 语音消息
     */
    VOICE_MESSAGE(5, "语音消息"),

    /**
     * 心跳消息
     */
    HEART_BEAT(6, "心跳消息");

    private static final Map<Integer, ChatEnum> CHAT_ENUM_MAP;

    static{
        CHAT_ENUM_MAP = Arrays.stream(ChatEnum.values())
                .collect(Collectors.toMap(ChatEnum::getType,
                        Function.identity(),
                        (firstEnum, secondEnum) -> firstEnum,
                        () -> CollectionUtils.newHashMap(ChatEnum.values().length)));
    }


    private final Integer type;

    private final String description;

    public static ChatEnum get(Integer type){
        ChatEnum chatEnum = CHAT_ENUM_MAP.get(type);
        if(chatEnum == null){
            throw new RuntimeException("聊天类型不存在");
        }
        return chatEnum;
    }
}
