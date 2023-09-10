package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.entity.RoleMenu;
import com.fly.mapper.RoleMenuMapper;
import com.fly.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * @author Milk
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
}
