package com.fly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.dto.talk.TalkDTO;
import com.fly.dto.talk.TalkMangeDTO;
import com.fly.entity.Talk;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Milk
 */
@Mapper
public interface TalkMapper extends BaseMapper<Talk> {
    /**
     * 获取说说列表
     */
    List<TalkMangeDTO> listAdminTalks(long offset, long size, Integer status);

    /**
     * 前台列出所有的说说
     */
    List<TalkDTO> listTalks(long offset, long size);
}
