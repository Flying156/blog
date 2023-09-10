package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.userAuth.AreaCountDTO;
import com.fly.dto.userAuth.UserDTO;
import com.fly.entity.UserAuth;
import com.fly.vo.user.PasswordVO;
import com.fly.vo.user.UserAuthVO;
import com.fly.vo.user.UserSearchVO;

import java.util.List;

/**
 * @author Milk
 */
public interface UserAuthService extends IService<UserAuth> {
    /**
     * 显示用户来源于哪个地区
     */
    List<AreaCountDTO> listUserAreas(Integer userType);

    /**
     * 分页查询
     */
    PageDTO<UserDTO> listUsers(UserSearchVO userSearchVO);

    /**
     * 更改用户密码
     * @param passwordVO 密码
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 发送验证码
     * @param email 邮箱
     */
    void sendVerificationCode(String email);

    /**
     * 用户注册
     * @param userAuthVO 注册信息
     */
    void register(UserAuthVO userAuthVO);
}
