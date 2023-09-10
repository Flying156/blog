package com.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.dto.resource.ResourceMangeDTO;
import com.fly.dto.resource.RoleResourceDTO;
import com.fly.entity.Resource;
import com.fly.vo.resource.ResourceVO;

import java.util.List;

public interface ResourceService extends IService<Resource> {

    /**
     * 角色资源权限列表
     */
    List<RoleResourceDTO> listRoleResources();

    /**
     * 在资源管理页面列出资源列表
     */
    List<ResourceMangeDTO> listResources(String keywords);

    /**
     * 修改或新增资源
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 移除资源
     * @param resourceId 资源 ID
     */
    void removeResources(Integer resourceId);
}
