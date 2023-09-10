package com.fly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.dto.menu.MenuBranchDTO;
import com.fly.dto.menu.MenuMangeDTO;
import com.fly.dto.menu.UserMenuDTO;
import com.fly.dto.role.RoleMenuDTO;
import com.fly.entity.Menu;
import com.fly.entity.RoleMenu;
import com.fly.mapper.MenuMapper;
import com.fly.mapper.RoleMenuMapper;
import com.fly.service.MenuService;
import com.fly.util.*;
import com.fly.vo.menu.MenuHiddenVO;
import com.fly.vo.menu.MenuVO;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fly.constant.CacheConst.MENU_WITHOUT_HIDDEN;
import static com.fly.constant.CacheConst.MENU_WITH_HIDDEN;
import static com.fly.constant.GenericConst.EMPTY_STR;
import static com.fly.constant.GenericConst.TRUE_OF_INT;
import static com.fly.constant.WebsiteConst.*;

/**
 * @author Milk
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    /**
     * 用户登录时查询菜单列表
     * @return 菜单列表
     */
    @Override
    public List<UserMenuDTO> listUserMenu() {

        Integer userInfoId = SecurityUtils.getUserInfoId();
        // 通过用户id查询菜单列表
        List<Menu> menuList = baseMapper.listMenusByUserInfoId(userInfoId);
        // 转换为用户菜单列表
        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(menuList);

        return convertUserMenuList(menuBranchDTO);
    }

    /**
     * 在用户界面查询菜单列表
     * @return 菜单列表
     */
    @Override
    @Cacheable(cacheNames = MENU_WITHOUT_HIDDEN, key = "#root.methodName", sync = true)
    public List<RoleMenuDTO> listRoleMenus() {

        LambdaQueryWrapper<Menu> lq = new LambdaQueryWrapper<>();
        lq.select(Menu::getId, Menu::getName, Menu::getOrderNum, Menu::getParentId);

        List<Menu> menuList = baseMapper.selectList(lq);

        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(menuList);

        return convertRoleMenuList(menuBranchDTO);
    }

    /**
     * 获取菜单列表
     * @param keywords 菜单名称
     * @return 菜单列表
     */
    @Override
    @Cacheable(cacheNames = MENU_WITH_HIDDEN, key = "#root.methodName", sync = true)
    public List<MenuMangeDTO> listMenus(String keywords) {
        List<Menu> menuList = lambdaQuery().like(StrRegexUtils.isNotBlank(keywords), Menu::getName, keywords).list();

        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(menuList);

        return convertMangeMenuList(menuBranchDTO);
    }

    /**
     * 修改或新增菜单
     * @param menuVO 菜单数据
     */
    @Override
    @CacheEvict(cacheNames = {MENU_WITH_HIDDEN, MENU_WITHOUT_HIDDEN}, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateMenu(MenuVO menuVO) {
        saveOrUpdate(BeanCopyUtils.copy(menuVO, Menu.class));
    }

    /**
     * 删除菜单及其子菜单
     * @param menuId 菜单 ID
     */
    @Override
    @CacheEvict(cacheNames = {MENU_WITH_HIDDEN, MENU_WITHOUT_HIDDEN}, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeMenus(Integer menuId) {
        // 菜单管理不能删除
        if (menuId.equals(MENU_MANAGE_ID)
                || menuId.equals(AUTHORITY_MANAGE_ID)) {
            throw new ServiceException("菜单管理不能删除");
        }
        List <Integer> menuIdList = getBranchIdSubmenuIdList(menuId);
        // 查询菜单是否与角色关联
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleMenu::getMenuId, menuIdList);
        boolean exists = roleMenuMapper.exists(queryWrapper);
        if(exists){
            throw new ServiceException("有角色关联菜单或子菜单");
        }
        // 删除菜单及其子菜单
        removeBatchByIds(menuIdList);
    }

    /**
     * 修改菜单隐藏状态
     * @param menuHiddenVO 修改的菜单数据
     */
    @Override
    @CacheEvict(cacheNames = MENU_WITH_HIDDEN, allEntries = true)
    public void updateHiddenStatus(MenuHiddenVO menuHiddenVO) {
        // 菜单管理不能隐藏
        Integer menuId = menuHiddenVO.getMenuId();
        if (menuId.equals(MENU_MANAGE_ID)
                || menuId.equals(AUTHORITY_MANAGE_ID)) {
            throw new ServiceException("菜单管理不能隐藏");
        }

        LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Menu::getIsHidden, menuHiddenVO.getIsHidden());
        updateWrapper.eq(Menu::getId, menuHiddenVO.getMenuId());
        // 如果修改目录，同时修改目录下菜单
        if(menuHiddenVO.getParentId() == null){
            updateWrapper.or().eq(Menu::getParentId, menuId);
        }
        update(updateWrapper);
    }

    /**
     * 获取子菜单及其本身的 ID 列表
     */
    private List<Integer> getBranchIdSubmenuIdList(Integer menuId) {
        List<Integer> menuIdList = lambdaQuery()
                .select(Menu::getId)
                .eq(Menu::getParentId, menuId)
                .list().stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
        menuIdList.add(menuId);
        return menuIdList;
    }

    /**
     * 将数据转化成实体类所要求的数据
     */
    private List<MenuMangeDTO> convertMangeMenuList(MenuBranchDTO menuBranchDTO) {
        Map<Integer, List<Menu>> branchIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();
        List<Menu> menuList = menuBranchDTO.getMenuBranchList();

        return menuList.stream().map(menu ->{
            List<MenuMangeDTO> children = ConvertUtils.convertList(branchIdSubmenuMap.get(menu.getId()), MenuMangeDTO.class);

            MenuMangeDTO menuMangeDTO = BeanCopyUtils.copy(menu, MenuMangeDTO.class);
            menuMangeDTO.setChildren(children);
            return menuMangeDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 转换角色菜单数据列表,转换成前端接受的数据
     */
    private List<RoleMenuDTO> convertRoleMenuList(MenuBranchDTO menuBranchDTO) {
        Map<Integer, List<Menu>> branchIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();

        List<Menu> menuBranchList = menuBranchDTO.getMenuBranchList();
        return menuBranchList.stream().map(branch ->{
            List<RoleMenuDTO> children = Optional.ofNullable(branchIdSubmenuMap.get(branch.getId()))
                    .orElseGet(ArrayList::new)
                    .stream().map(child -> RoleMenuDTO.builder()
                                .id(child.getId())
                                .label(child.getName())
                                .build()
                    ).collect(Collectors.toList());
            return RoleMenuDTO.builder()
                    .id(branch.getId())
                    .label(branch.getName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());

    }

    /**
     * 装换成为前端接受的数据
     */
    private List<UserMenuDTO> convertUserMenuList(MenuBranchDTO menuBranchDTO){
        Map<Integer, List<Menu>>brandIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();
        List<Menu> menuBranchList = menuBranchDTO.getMenuBranchList();

        return menuBranchList.stream().map(branch ->{
            // 通过父节点的id查找对应的子节点
            List<Menu> sumbemnuList = brandIdSubmenuMap.get(branch.getId());
            // 如果菜单不为空，
            if(CollectionUtils.isNotEmpty(sumbemnuList)){
                return convertCatalogAndSubmenu(branch, sumbemnuList);
            } else {
                // 空菜单
                return convertEmptyCatalogOrLevelOneMenu(branch);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 装换子菜单信息
     * @param branch 父节点
     * @param submenuList 子菜单
     */
    private UserMenuDTO convertCatalogAndSubmenu (Menu branch, List<Menu> submenuList){
        List<UserMenuDTO> children = submenuList
                .stream()
                .sorted(Comparator.comparingInt(Menu::getOrderNum))
                .map(submenu -> {
                    UserMenuDTO child = BeanCopyUtils.copy(submenu, UserMenuDTO.class);
                    // 隐藏状态字段名不一样
                    child.setHidden(TRUE_OF_INT.equals(submenu.getIsHidden()));
                    return child;
                })
                .collect(Collectors.toList());
        UserMenuDTO userMenuDTO = BeanCopyUtils.copy(branch, UserMenuDTO.class);
        userMenuDTO.setHidden(TRUE_OF_INT.equals(branch.getIsHidden()));
        userMenuDTO.setChildren(children);
        return userMenuDTO;
    }

    /**
     * 空目录转换
     */
    private UserMenuDTO convertEmptyCatalogOrLevelOneMenu(Menu levelOneMenu){
        List<UserMenuDTO> children = Collections.singletonList
                (UserMenuDTO.builder()
                        .name(levelOneMenu.getName())
                        .path(EMPTY_STR)
                        .component(levelOneMenu.getComponent())
                        .icon(levelOneMenu.getIcon())
                        .build());
        return UserMenuDTO.builder()
                .path(levelOneMenu.getPath())
                .component(LAYOUT)
                .hidden(levelOneMenu.getIsHidden().equals(TRUE_OF_INT))
                .children(children)
                .build();
    }


    /**
     * 得到子树，以及父节点和子树对应的关系
     */
    private MenuBranchDTO getMenuBranchDTO(List<Menu> menuList){

        CompletableFuture<Map<Integer, List<Menu>>> future
                = AsyncUtils.supplyAsync(() -> getBranchIdSubmenuMap(menuList));
        // 获取每个分支的父节点和子菜单的映射
        List<Menu> catalogList = listBranchIds(menuList);
        Map<Integer, List<Menu>> branchIdSubmenuMap
                = AsyncUtils.get(future, "获取分支 ID 和子菜单列表的映射");
        return new MenuBranchDTO(catalogList, branchIdSubmenuMap);
    }

    /**
     * 通过stream流获取当前父节点的ID
     */
    private List<Menu> listBranchIds(List<Menu> menuList){
        return menuList.stream()
                .filter(menu -> Objects.isNull(menu.getParentId()))
                .sorted(Comparator.comparingInt(Menu::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 得到根节点和子树的对应关系
     */
    public Map<Integer, List<Menu>> getBranchIdSubmenuMap(List<Menu> menuList){
        return menuList.stream()
                .filter(menu -> Objects.nonNull(menu.getParentId()))
                .collect(Collectors.groupingBy(Menu::getParentId));
    }
}
