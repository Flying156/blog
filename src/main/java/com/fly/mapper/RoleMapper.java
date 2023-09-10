package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.role.ResourceRoleDTO;
import com.fly.dto.role.RoleMangeDTO;
import com.fly.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据用户ID 列出角色权限,不会查询被禁用的权限角色
     * @param userInfoId 用户 ID
     */
    List<String> listAuthorityRoles(Integer userInfoId);

    /**
     * 后台查询 角色 列表
     * @param keywords 角色名称
     */
    List<RoleMangeDTO> listManageRoles(Long offset, Long size, @Param(value = "keywords")String keywords);


    /**
     * 在用户授权管理器中查询角色和资源的映射
     */
    List<ResourceRoleDTO> listRoleResource();
}
