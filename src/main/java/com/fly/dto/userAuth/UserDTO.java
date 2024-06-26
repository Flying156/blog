package com.fly.dto.userAuth;

import com.fly.dto.role.UserRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用户列表展示类
 * @author Milk
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    /**
     * 用户认证 ID
     */
    private Integer id;

    /**
     * 用户信息 ID
     */
    private Integer userInfoId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户角色
     */
    private List<UserRoleDTO> roleList;

    /**
     * 登录类型
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    /**
     * 用户评论状态
     */
    private Integer isDisable;

    // unused
    /**
     * 状态
     */
    private Integer status;

}
