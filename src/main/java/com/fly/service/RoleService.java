package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.article.PageDTO;
import com.fly.dto.role.RoleMangeDTO;
import com.fly.dto.role.UserRoleDTO;
import com.fly.entity.Role;
import com.fly.vo.role.DisableRoleVO;
import com.fly.vo.role.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {


    /**
     * 后台查询角色列表
     * @param keywords 关键词
     * @return 角色列表
     */
    PageDTO<RoleMangeDTO> listRoleManagement(String keywords);

    /**
     * 添加或更新角色
     * @param roleVO 角色相关权限和列表
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     * @param roleIdList 角色 ID 列表
     */
    void removeRoles(List<Integer> roleIdList);

    /**
     * 修改用户角色禁用状态
     * @param disableRoleVO 角色 ID， 禁用状态
     */
    void updateDisableStatus(DisableRoleVO disableRoleVO);

    /**
     * 根据用户查询角色
     * @return 用户角色列表
     */
    List<UserRoleDTO> listUserRoles();
}
