package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.userInfo.UserOnlineDTO;
import com.fly.entity.UserInfo;
import com.fly.vo.user.UserDisableVO;
import com.fly.vo.user.UserInfoVO;
import com.fly.vo.user.UserRoleVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Milk
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户账号禁用
     * @param userDisableVO 用户账号信息
     */
    void disableUser(UserDisableVO userDisableVO);

    /**
     * 分页查询在线用户
     * @param keywords 用户名称
     * @return 在线用户列表
     */
    PageDTO<UserOnlineDTO> listOnlineUsers(String keywords);

    /**
     * 下线用户
     * @param userInfoId 用户 ID
     */
    void makeUserOffLine(Integer userInfoId);

    /**
     * 更新用户信息
     * @param userInfoVO 用户信息
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 修改用户昵称或角色
     * @param userRoleVO 用户昵称及角色 ID
     */
    void updateNicknameOrRole(UserRoleVO userRoleVO);

    /**
     * 上传用户头像
     * @param multipartFile 照片文件
     * @return  文件链接
     */
    String uploadUserAvatar(MultipartFile multipartFile) ;
}
