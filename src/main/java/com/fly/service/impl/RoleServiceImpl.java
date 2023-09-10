package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.article.PageDTO;
import com.fly.dto.role.RoleMangeDTO;
import com.fly.dto.role.UserRoleDTO;
import com.fly.entity.Role;
import com.fly.entity.RoleMenu;
import com.fly.entity.RoleResource;
import com.fly.entity.UserRole;
import com.fly.mapper.RoleMapper;
import com.fly.mapper.UserRoleMapper;
import com.fly.service.RoleMenuService;
import com.fly.service.RoleResourceService;
import com.fly.service.RoleService;
import com.fly.util.*;
import com.fly.vo.role.DisableRoleVO;
import com.fly.vo.role.RoleVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.fly.constant.CacheConst.ROLE;
import static com.fly.constant.GenericConst.ZERO_L;
import static com.fly.constant.WebsiteConst.ADMIN_ID;

/**
 * @author Milk
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleResourceService roleResourceService;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public PageDTO<RoleMangeDTO> listRoleManagement(String keywords) {
        Long roleCount = lambdaQuery()
                .eq(StrRegexUtils.isNotBlank(keywords), Role::getRoleName, keywords)
                .count();
        if(roleCount.equals(ZERO_L)){
            return new PageDTO<>();
        }

        List<RoleMangeDTO> roleMangeDTOList = baseMapper.listManageRoles(PageUtils.offset(),PageUtils.size(), keywords);
        return PageUtils.build(roleMangeDTOList, roleCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateRole(RoleVO roleVO) {
        Integer roleId = roleVO.getId();
        List<Integer> menuIdList = roleVO.getMenuIdList();
        List<Integer> resourceIdList= roleVO.getResourceIdList();
        if(roleId == null){
            // 查看角色名称是否重复
            boolean exists = lambdaQuery().eq(Role::getRoleName, roleVO.getRoleName()).exists();
            if(exists){
                throw new ServiceException("角色名已存在");
            }
        }else{
            // 删除角色原来的映射
            removeSingleRoleMap(roleId, menuIdList, resourceIdList);
        }
        Role role = BeanCopyUtils.copy(roleVO, Role.class);
        saveOrUpdate(role);
        // 新建角色和权限、资源的映射
        saveRoleMap(role.getId(), menuIdList, resourceIdList);
        // 清除 Spring Security 的权限授权器
        SecurityUtils.clearAuthorizationCredentials();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = ROLE, allEntries = true)
    public void removeRoles(List<Integer> roleIdList) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserRole::getRoleId, roleIdList);
        boolean exists = userRoleMapper.exists(queryWrapper);
        if(exists){
            throw new ServiceException("有用户关联角色");
        }
        // 删除角色
        removeBatchByIds(roleIdList);
        // 删除角色的映射
        removeMultiRoleMap(roleIdList);
    }

    @Override
    @CacheEvict(cacheNames = ROLE, allEntries = true)
    public void updateDisableStatus(DisableRoleVO disableRoleVO) {
        Integer roleId = disableRoleVO.getRoleId();
        if(roleId.equals(ADMIN_ID)){
            throw new ServiceException("管理员不能被禁用");
        }
        // 修改禁用状态
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Role::getIsDisable, disableRoleVO.getIsDisable());
        updateWrapper.eq(Role::getId, roleId);
        update(updateWrapper);
        // 清除 Spring Security 的授权凭据
        SecurityUtils.clearAuthorizationCredentials();

    }

    @Override
    @Cacheable(cacheNames = ROLE, key = "#root.methodName", sync = true)
    public List<UserRoleDTO> listUserRoles() {
        List<Role> roleList = lambdaQuery()
                .select(Role::getId, Role::getRoleName).list();
        return ConvertUtils.convertList(roleList, UserRoleDTO.class);
    }

    private void saveRoleMap(Integer id, List<Integer> menuIdList, List<Integer> resourceIdList) {
        if(CollectionUtils.isNotEmpty(menuIdList)){
            // 添加角色与菜单的映射
            List<RoleMenu> roleMenuList = menuIdList
                    .stream()
                    .map(menuId -> RoleMenu.builder()
                            .menuId(menuId)
                            .roleId(id)
                            .build()
                    ).collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }
        if(CollectionUtils.isNotEmpty(resourceIdList)){
            // 添加角色与资源的映射
            List<RoleResource> roleResourceList = resourceIdList
                    .stream()
                    .map(resourceId -> RoleResource.builder()
                            .resourceId(resourceId)
                            .roleId(id)
                            .build()
                    ).collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);
        }
    }

    /**
     * 解除原有的权限和资源关系
     */
    private void removeSingleRoleMap(Integer roleId, List<Integer> menuIdList, List<Integer> resourceIdList) {
        if(CollectionUtils.isNotEmpty(menuIdList)) {
            // 删除角色与菜单的映射
            roleMenuService.lambdaUpdate()
                    .eq(RoleMenu::getRoleId, roleId)
                    .remove();
        }else if(CollectionUtils.isNotEmpty(resourceIdList)) {
            // 删除角色与资源的映射
            roleResourceService.lambdaUpdate()
                    .eq(RoleResource::getRoleId, roleId)
                    .remove();
        }
    }
    /**
     * 删除角色与菜单，资源关联的关系
     */
    private void removeMultiRoleMap(List<Integer> roleIdList){
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            roleResourceService.lambdaUpdate()
                    .in(RoleResource::getRoleId, roleIdList)
                    .remove();

            roleMenuService.lambdaUpdate()
                    .in(RoleMenu::getRoleId, roleIdList)
                    .remove();
        }
    }
}
