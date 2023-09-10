package com.fly.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户登录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "tb_user_auth")
public class UserAuth {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户信息ID
     */
    private Integer userInfoId;
    /**
     * 用户名（邮箱）,由于security需要使用用户名
     * 所以将邮箱封装为用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录类型
     */
    private Integer loginType;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * IP来源
     */
    private String ipSource;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 操作系统
     */
    private String os;
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
    /**
     * 上次登陆时间
     */
    private LocalDateTime lastLoginTime;
}
