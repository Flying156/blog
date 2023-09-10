package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.resource.ResourceMangeDTO;
import com.fly.dto.resource.ResourceModuleDTO;
import com.fly.dto.resource.RoleResourceDTO;
import com.fly.entity.Resource;
import com.fly.entity.RoleResource;
import com.fly.mapper.ResourceMapper;
import com.fly.mapper.RoleResourceMapper;
import com.fly.service.ResourceService;
import com.fly.util.AsyncUtils;
import com.fly.util.BeanCopyUtils;
import com.fly.util.ConvertUtils;
import com.fly.util.StrRegexUtils;
import com.fly.vo.resource.ResourceVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.CacheConst.RESOURCE;
import static com.fly.constant.GenericConst.FALSE_OF_INT;

/**
 * @author Milk
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService{

    @javax.annotation.Resource
    private RoleResourceMapper roleResourceMapper;

    @Override
    @Cacheable(cacheNames = RESOURCE, key = "#root.methodName", sync = true)
    public List<RoleResourceDTO> listRoleResources() {
        List<Resource> resourceList = lambdaQuery()
                .select(Resource::getId, Resource::getResourceName, Resource::getParentId)
                .eq(Resource::getIsAnonymous, FALSE_OF_INT)
                .list();
        ResourceModuleDTO resourceModuleDTO = getResourceModuleDTO(resourceList);
        return convertRoleResourceList(resourceModuleDTO);
    }

    @Override
    @Cacheable(cacheNames = RESOURCE, key = "#root.methodName", sync = true,
    condition = "T(com.fly.util.StrRegexUtils).isBlank(#keywords)")
    public List<ResourceMangeDTO> listResources(String keywords) {
        List<Resource> resourceList = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords), Resource::getResourceName, keywords)
                .list();

        ResourceModuleDTO resourceModuleDTO = getResourceModuleDTO(resourceList);

        return convertResourceList(resourceModuleDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = RESOURCE, allEntries = true)
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        saveOrUpdate(BeanCopyUtils.copy(resourceVO, Resource.class));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = RESOURCE, allEntries = true)
    public void removeResources(Integer resourceId) {
        // 获取资源模块和子资源 ID 列表
        List<Integer> resourceIdList = getModuleAndChildrenIdList(resourceId);
        // 判断角色是否与资源有关
        LambdaQueryWrapper<RoleResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleResource::getId, resourceIdList);
        boolean exists = roleResourceMapper.exists(queryWrapper);
        if(exists){
            throw new ServiceException("有角色关联资源或子资源");
        }
        removeBatchByIds(resourceIdList);
    }

    /**
     * 获取子资源 ID
     */
    private List<Integer> getModuleAndChildrenIdList(Integer resourceId) {
        List<Integer> childIdList = lambdaQuery().eq(Resource::getParentId, resourceId)
                .list().stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
        childIdList.add(resourceId);
        return childIdList;
    }

    /**
     * 转换资源列表
     */
    private List<ResourceMangeDTO> convertResourceList(ResourceModuleDTO resourceModuleDTO) {
        Map<Integer, List<Resource>> moduleIdChildrenMap = resourceModuleDTO.getModuleIdChildrenMap();
        List<Resource> resourceModuleList = resourceModuleDTO.getResourceModuleList();

        return resourceModuleList.stream().map(resource -> {
            List<ResourceMangeDTO> children = ConvertUtils.convertList
                    (moduleIdChildrenMap.get(resource.getId()), ResourceMangeDTO.class);
            ResourceMangeDTO manageResourceDTO = BeanCopyUtils.copy
                    (resource, ResourceMangeDTO.class);
            manageResourceDTO.setChildren(children);
            return manageResourceDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 转换角色资源列表
     */
    private List<RoleResourceDTO> convertRoleResourceList(ResourceModuleDTO resourceModuleDTO) {
        Map<Integer, List<Resource>> moduleIdChildrenMap = resourceModuleDTO.getModuleIdChildrenMap();
        List<Resource> resourceModuleList = resourceModuleDTO.getResourceModuleList();
        // 组装资源模块和子资源数据
        return resourceModuleList.stream().map(branch ->{
            List<RoleResourceDTO> children = Optional.ofNullable(moduleIdChildrenMap.get(branch.getId()))
                    .orElseGet(ArrayList::new)
                    .stream()
                    .map(child -> RoleResourceDTO
                            .builder()
                            .id(child.getId())
                            .label(child.getResourceName())
                            .build()).collect(Collectors.toList());
            return RoleResourceDTO.builder()
                    .id(branch.getId())
                    .label(branch.getResourceName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取菜单模块数据
     */
    private ResourceModuleDTO getResourceModuleDTO(List<Resource> resourceList) {

        CompletableFuture<Map<Integer, List<Resource>>> future
               = AsyncUtils.supplyAsync(() -> getModuleIdChildrenMap(resourceList));
        List<Resource> resourceModuleList = listResourceModules(resourceList);
        Map<Integer, List<Resource>>  moduleIdChildrenMap
                = AsyncUtils.get(future, "获取模块 ID 与模块下资源的映射");
        return new ResourceModuleDTO(resourceModuleList, moduleIdChildrenMap);
    }

    /**
     * 获取资源父节点
     */
    private List<Resource> listResourceModules(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(resource -> Objects.isNull(resource.getParentId()))
                .collect(Collectors.toList());
    }


    /**
     * 获取模块 ID 与模块下的资源映射
     */
    private Map<Integer, List<Resource>> getModuleIdChildrenMap(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(resource -> Objects.nonNull(resource.getParentId()))
                .collect(Collectors.groupingBy(Resource::getParentId));
    }
}
