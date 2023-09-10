package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.constant.RedisConst;
import com.fly.dto.article.PageDTO;
import com.fly.dto.userInfo.UserOnlineDTO;
import com.fly.entity.UserAuth;
import com.fly.entity.UserDetail;
import com.fly.entity.UserInfo;
import com.fly.entity.UserRole;
import com.fly.mapper.UserAuthMapper;
import com.fly.mapper.UserInfoMapper;
import com.fly.service.UserInfoService;
import com.fly.service.UserRoleService;
import com.fly.util.PageUtils;
import com.fly.util.QiNiuUtils;
import com.fly.util.RedisUtils;
import com.fly.util.SecurityUtils;
import com.fly.vo.user.UserDisableVO;
import com.fly.vo.user.UserInfoVO;
import com.fly.vo.user.UserRoleVO;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Milk
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAuthMapper userAuthMapper;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private QiNiuUtils qiNiuUtils;

    @Override
    public void disableUser(UserDisableVO userDisableVO) {
        lambdaUpdate().set(UserInfo::getIsDisable, userDisableVO.getIsDisable())
                .eq(UserInfo::getId, userDisableVO.getId())
                .update();
    }

    @Override
    public PageDTO<UserOnlineDTO> listOnlineUsers(String keywords) {
        Set<String> userNameSet = SecurityUtils.getOnlineUsernames();
        IPage<UserOnlineDTO> page = baseMapper.listOnlineUsers
                (keywords, userNameSet, PageUtils.getPage());
        return PageUtils.build(page);
    }

    @Override
    public void makeUserOffLine(Integer userInfoId) {
        LambdaQueryWrapper<UserAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuth::getUserInfoId, userInfoId);
        String username =  userAuthMapper.selectOne(queryWrapper).getUsername();

        UserDetail userDetail = new UserDetail();
        userDetail.setUsername(username);

        // TODO 下线用户的所有会话
        SecurityUtils.getNonExpiredSessions(userDetail)
                        .forEach(SessionInformation::expireNow);
        // 删除下线的用户信息，用于会话控制
        RedisUtils.sRemove(RedisConst.ONLINE_USERNAME, username);
    }

    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        Integer userInfoId = 1;
        UserInfo userInfo = UserInfo.builder()
                .id(userInfoId)
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .build();
        updateById(userInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateNicknameOrRole(UserRoleVO userRoleVO) {
        Integer userInfoId = userRoleVO.getUserInfoId();

        lambdaUpdate().set(UserInfo::getNickname, userRoleVO.getNickname())
                .eq(UserInfo::getId, userInfoId).update();

        if(CollectionUtils.isNotEmpty(userRoleVO.getRoleIdList())){
            // 首先移除所有用户和角色的映射关系
            userRoleService.lambdaUpdate()
                    .eq(UserRole::getUserId, userInfoId)
                    .remove();

            // 插入用户和角色的关系
            List<UserRole> userRoleList = userRoleVO.getRoleIdList()
                    .stream()
                    .map(roleId -> UserRole.builder()
                            .userId(userInfoId)
                            .roleId(roleId)
                            .build())
                    .collect(Collectors.toList());
            userRoleService.saveBatch(userRoleList);
        }
    }

    @Override
    public String uploadUserAvatar(MultipartFile multipartFile){
        String avatarUrl = qiNiuUtils.uploadImage(multipartFile);
        lambdaUpdate()
                .eq(UserInfo::getId, SecurityUtils.getUserInfoId())
                .set(UserInfo::getAvatar, avatarUrl)
                .update();
        return avatarUrl;
    }
}
