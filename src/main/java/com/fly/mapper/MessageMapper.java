package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Milk
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
