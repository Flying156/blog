package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.blogInfo.WebsiteConfig;
import com.fly.dto.comment.EmailDTO;
import com.fly.dto.userAuth.AreaCountDTO;
import com.fly.dto.userAuth.UserDTO;
import com.fly.entity.UserAuth;
import com.fly.entity.UserDetail;
import com.fly.entity.UserInfo;
import com.fly.entity.UserRole;
import com.fly.enums.LoginTypeEnum;
import com.fly.enums.RoleEnum;
import com.fly.enums.UserTypeEnum;
import com.fly.mapper.UserAuthMapper;
import com.fly.mapper.UserRoleMapper;
import com.fly.schedule.AreaCountSchedule;
import com.fly.service.UserAuthService;
import com.fly.service.UserInfoService;
import com.fly.util.*;
import com.fly.vo.user.PasswordVO;
import com.fly.vo.user.UserAuthVO;
import com.fly.vo.user.UserSearchVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.fly.constant.GenericConst.ZERO_L;
import static com.fly.constant.RedisConst.CODE_EXPIRE_TIME;
import static com.fly.constant.RedisConst.CODE_PREFIX;
import static com.fly.enums.UserTypeEnum.USER;
import static com.fly.enums.UserTypeEnum.VISITOR;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Milk
 */
@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserRoleMapper userRoleMapper;


    @Override
    public List<AreaCountDTO> listUserAreas(Integer userType) {
        UserTypeEnum typeEnum = UserTypeEnum.get(userType);
        if(USER.equals(typeEnum)){
            return AreaCountSchedule.getUserAreaCount();
        }else if(VISITOR.equals(typeEnum)){
            return AreaCountSchedule.getVisitorAreaCount();
        }else{
            throw new RuntimeException("用户类型不存在");
        }
    }

    @Override
    public PageDTO<UserDTO> listUsers(UserSearchVO userSearchVO) {
        // 根据条件查询后台用户总数
        Long count = baseMapper.countUsers(userSearchVO);
        if (count.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询分页的用户列表数据
        List<UserDTO> userDTOList = baseMapper.listUsers
                (PageUtils.offset(), PageUtils.size(), userSearchVO);
        return PageUtils.build(userDTOList, count);
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 需要验证旧密码是否正确
        Integer userAuthId = SecurityUtils.getInfo(UserDetail::getId);
        UserAuth userAuth = getById(userAuthId);
        if(userAuth == null){
            throw new ServiceException("用户不存在");
        }
        if(!SecurityUtils.matches(passwordVO.getOldPassword(), userAuth.getPassword())){
            throw new ServiceException("旧密码不存在");
        }
        // 更新密码
        LambdaUpdateWrapper<UserAuth> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserAuth::getPassword, SecurityUtils.encode(passwordVO.getNewPassword()));
        updateWrapper.eq(UserAuth::getId, userAuthId);
        update(updateWrapper);
        // 需要下线用户以验证密码
        userInfoService.makeUserOffLine(userAuth.getUserInfoId());
    }

    @Override
    public void sendVerificationCode(String email) {
        if(!StrRegexUtils.checkEmail(email)){
            throw new ServiceException("请输入正确的邮箱");
        }
        // 获取随机验证码
        String randomCode = SecurityUtils.getRandomCode();

        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("博客验证码")
                .content("您的验证码为 " + randomCode + "，有效期 15 分钟。")
                .build();

        RabbitMQUtils.sendEmail(emailDTO);

        String codeKey = CODE_PREFIX + email;
        RedisUtils.setEx(codeKey, randomCode, CODE_EXPIRE_TIME, MINUTES);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void register(UserAuthVO userAuthVO) {
        // 检查用户认证数据
        if (checkUserAuthVO(userAuthVO)) {
            throw new ServiceException("邮箱已经注册");
        }

        // 新增用户信息
        UserInfo userInfo = UserInfo.builder()
                .avatar(ConfigUtils.getCache(WebsiteConfig::getUserAvatar))
                .email(userAuthVO.getUsername())
                .nickname(SecurityUtils.getUniqueName())
                .build();
        userInfoService.save(userInfo);

        // 绑定用户角色
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
        // 新建用户账号
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .password(SecurityUtils.encode(userAuthVO.getPassword()))
                .username(userAuthVO.getUsername())
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        baseMapper.insert(userAuth);

    }

    private boolean checkUserAuthVO(UserAuthVO userAuthVO) {

        String username = userAuthVO.getUsername();
        Object code = RedisUtils.get(CODE_PREFIX + username);
        if(!userAuthVO.getCode().equals(code)){
            throw new ServiceException("验证码错误");
        }
        // 查询邮箱是否存在
        return lambdaQuery()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, username)
                .exists();
    }
}
