package com.fly.dto.friendLink;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台友链数据
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendLinkDTO {

    /**
     * 链接 ID
     */
    private Integer id;

    /**
     * 链接名
     */
    private String linkName;

    /**
     * 链接头像
     */
    private String linkAvatar;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 介绍
     */
    private String linkIntro;

}
