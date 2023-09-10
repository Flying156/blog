package com.fly.entity;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fly.constant.GenericConst;
import com.fly.security.CustomizedGrantedAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实现Spring Security中的用户类，实现自定义
 *
 * @author Milk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail implements UserDetails {
    // region UserAuth
    /**
     * 用户认证 ID
     */
    private Integer id;
    /**
     * 用户账号信息 ID
     */
    private Integer userInfoId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录方式
     */
    private Integer loginType;
    /**
     * IP 地址
     */
    private String ipAddress;
    /**
     * IP 来源
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
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

    // region UserInfo
    /**
     * 邮箱号
     */
    private String email;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户简介
     */
    private String intro;
    /**
     * 个人网站
     */
    private String webSite;
    /**
     * 是否禁用（0 否 1 是）
     */
    private Integer isDisable;

    /**
     * 权限角色
     */
    private List<String> roleList;

    /**
     * 点赞文章 ID 集合
     */
    private Set<Object> articleLikeSet;
    /**
     * 点赞评论 ID 集合
     */
    private Set<Object> commentLikeSet;
    /**
     * 点赞说说 ID 集合
     */
    private Set<Object> talkLikeSet;

    /**
     *  权限列表
     */
    @Override
    public Collection<CustomizedGrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        return roleList.stream()
                .sorted(Comparator.naturalOrder())
                .map(CustomizedGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 账号是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return CollectionUtils.isNotEmpty(roleList);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否启用
     */
    @Override
    public boolean isEnabled() {
        return GenericConst.FALSE_OF_INT.equals(isDisable);
    }
}
