package com.fly.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台查看评论列表
 *
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageAdminDTO {


    /**
     * ID
     */
    private Integer id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 留言内容
     */
    private String messageContent;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * IP 地区
     */
    private String ipSource;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否审核
     */
    private Integer isReview;

    /**
     * 留言时间
     */
    private LocalDateTime createTime;
}
