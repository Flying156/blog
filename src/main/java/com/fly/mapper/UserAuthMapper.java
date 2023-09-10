package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.userAuth.UserDTO;
import com.fly.entity.UserAuth;
import com.fly.vo.user.UserSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author Milk
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {
    /**
     * 根据条件查询用户数量
     * @param userSearchVO 条件
     * @return 用户数量
     */
    Long countUsers( @Param("userSearchVO") UserSearchVO userSearchVO);

    /**
     * 分页查询用户
     * @param offset 第几页
     * @param size 页数
     * @param userSearchVO 查询条件
     * @return 用户列表
     */
    List<UserDTO> listUsers(@Param("offset") long offset,
                            @Param("size") long size,
                            @Param("userSearchVO") UserSearchVO userSearchVO);
}
