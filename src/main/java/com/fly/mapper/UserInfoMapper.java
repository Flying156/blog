package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fly.dto.userInfo.UserOnlineDTO;
import com.fly.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author Milk
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /**
     * 分页查询在线用户
     *
     * @param keywords    关键字
     * @param userNameSet 在线用户集合
     * @return   在线用户数据列表
     */
    IPage<UserOnlineDTO> listOnlineUsers(@Param("keywords") String keywords,
                                         @Param("userNameSet") Set<String> userNameSet,
                                         @Param("page") IPage<UserOnlineDTO> page);
}
