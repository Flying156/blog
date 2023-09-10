package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fly.entity.UserAuth;
import com.fly.entity.UserDetail;
import com.fly.entity.UserInfo;
import com.fly.mapper.RoleMapper;
import com.fly.mapper.UserAuthMapper;
import com.fly.mapper.UserInfoMapper;
import com.fly.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.fly.constant.GenericConst.*;
import static com.fly.constant.RedisConst.*;

/**
 * 实现Security的方法，从数据库查询
 * @author Milk
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 查询用户
     * @param username 用户名
     */
    @Override
    public UserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        if(StrRegexUtils.isBlank(username)){
            throw new UsernameNotFoundException("用户名不能为空");
        }
        UserAuth userAuth = new LambdaQueryChainWrapper<>(userAuthMapper)
                .select(UserAuth::getId, UserAuth::getUserInfoId,
                        UserAuth::getUsername, UserAuth::getPassword,
                        UserAuth::getLoginType)
                .eq(UserAuth::getUsername, username).one();


        if(userAuth == null){
            throw new UsernameNotFoundException("用户不存在");
        }

        return loadUserDetails(userAuth);
    }

    @SuppressWarnings("all")
    public UserDetail loadUserDetails(UserAuth userAuth){
        Integer userInfoId = userAuth.getUserInfoId();
        // 查询用户点赞消息
        Supplier<List> likeSetSupplier = () ->
                RedisUtils.executePipelined(new SessionCallback() {
                    // 使用管道可以大幅度加快多个 redis 的命令执行速度，
                    @Override
                    public Object execute(RedisOperations operations) throws DataAccessException {
                        SetOperations opsForSet = operations.opsForSet();
                        opsForSet.members(ARTICLE_LIKE_PREFIX + userInfoId);
                        opsForSet.members(COMMENT_LIKE_COUNT + userInfoId);
                        opsForSet.members(TALK_LIKE_PREFIX + userInfoId);
                        return null;
                    }
                });
        CompletableFuture<List> likeSetListFuture = AsyncUtils.supplyAsync(likeSetSupplier);
        // 查询用户相关信息
        UserInfo userInfo = userInfoMapper.selectById(userInfoId);

        // 查询用户权限角色
        List<String> roleList = roleMapper.listAuthorityRoles(userInfoId);

        // 获取当前的 HTTP 请求
        HttpServletRequest request = WebUtils.getCurrentRequest();
        // 获取操作系统
        String osName = WebUtils.getOsName(request);
        // 获取浏览器
        String browserName = WebUtils.getBrowserName(request);
        // 获取IP
        String ipAddress = WebUtils.getIpAddress(request);
        String ipSource = WebUtils.getIpSource(ipAddress);

        // 获取点赞信息
        List<Set<Object>> likeSetList = AsyncUtils.get
                (likeSetListFuture, "查询用户点赞信息异常");
        Set<Object> articleLikeSet = likeSetList.get(ZERO);
        Set<Object> commentLikeSet = likeSetList.get(ONE);
        Set<Object> talkLikeSet = likeSetList.get(TWO);
        // 封装用户信息
        return UserDetail.builder()
                .id(userAuth.getId())
                .userInfoId(userInfoId)
                .username(userAuth.getUsername())
                .password(userAuth.getPassword())
                .loginType(userAuth.getLoginType())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .lastLoginTime(TimeUtils.now())
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .isDisable(userInfo.getIsDisable())
                .browser(browserName)
                .os(osName)
                .roleList(roleList)
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .talkLikeSet(talkLikeSet)
                .build();
    }
}
