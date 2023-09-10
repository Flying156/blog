package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据用户ID查询菜单
     */
    List<Menu> listMenusByUserInfoId(Integer userInfoId);
}
