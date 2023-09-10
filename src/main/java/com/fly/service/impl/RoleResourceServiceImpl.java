package com.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.entity.RoleResource;
import com.fly.mapper.RoleResourceMapper;
import com.fly.service.RoleResourceService;
import org.springframework.stereotype.Service;

/**
 * @author Milk
 */
@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService{
}
