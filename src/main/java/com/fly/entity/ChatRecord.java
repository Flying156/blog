package com.fly.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天记录
 *
 * @author Milk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_chat_record")
public class ChatRecord {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 用户昵称
     */
    private Integer nickName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 聊天内容
     */
    private String content;
    /**
     * ip地址
     */
    private String ipAddress;
    /**
     * ip来源
     */
    private String ipSource;
    /**
     * 类型
     */
    private String type;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
