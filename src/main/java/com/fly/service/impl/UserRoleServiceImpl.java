package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.entity.UserRole;
import com.fly.mapper.UserRoleMapper;
import com.fly.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author Milk
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
